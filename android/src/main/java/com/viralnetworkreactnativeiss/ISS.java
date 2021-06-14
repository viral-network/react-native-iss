/*
Permission is hereby granted, perpetual, worldwide, non-exclusive, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:



1. The Software cannot be used in any form or in any substantial portions for development, maintenance and for any other purposes, in the military sphere and in relation to military products, including, but not limited to:

a. any kind of armored force vehicles, missile weapons, warships, artillery weapons, air military vehicles (including military aircrafts, combat helicopters, military drones aircrafts), air defense systems, rifle armaments, small arms, firearms and side arms, melee weapons, chemical weapons, weapons of mass destruction;

b. any special software for development technical documentation for military purposes;

c. any special equipment for tests of prototypes of any subjects with military purpose of use;

d. any means of protection for conduction of acts of a military nature;

e. any software or hardware for determining strategies, reconnaissance, troop positioning, conducting military actions, conducting special operations;

f. any dual-use products with possibility to use the product in military purposes;

g. any other products, software or services connected to military activities;

h. any auxiliary means related to abovementioned spheres and products.



2. The Software cannot be used as described herein in any connection to the military activities. A person, a company, or any other entity, which wants to use the Software, shall take all reasonable actions to make sure that the purpose of use of the Software cannot be possibly connected to military purposes.



3. The Software cannot be used by a person, a company, or any other entity, activities of which are connected to military sphere in any means. If a person, a company, or any other entity, during the period of time for the usage of Software, would engage in activities, connected to military purposes, such person, company, or any other entity shall immediately stop the usage of Software and any its modifications or alterations.



4. Abovementioned restrictions should apply to all modification, alteration, merge, and to other actions, related to the Software, regardless of how the Software was changed due to the abovementioned actions.



The above copyright notice and this permission notice shall be included in all copies or substantial portions, modifications and alterations of the Software.



THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

package com.viralnetworkreactnativeiss;

import java.util.Arrays;

public class ISS {
  static final int NUMBER_OF_SECURITY_LEVELS = 3;
  static final int MAX_TRYTE_VALUE = 13;
  static final int MIN_TRYTE_VALUE = -MAX_TRYTE_VALUE;
  static final int BUNDLE_FRAGMENT_LENGTH = Curl_729_27.HASH_LENGTH / NUMBER_OF_SECURITY_LEVELS;
  static final int BUNDLE_FRAGMENT_TRYTE_LENGTH = BUNDLE_FRAGMENT_LENGTH / 3;
  static final int KEY_SIGNATURE_FRAGMENT_LENGTH = (BUNDLE_FRAGMENT_LENGTH / 3) * Curl_729_27.HASH_LENGTH;

  public static void subseed(final byte[] seed, final int index, final byte[] subseedTrits) {
    final int length = (int) (index > 0 ? 1 + Math.floor(Math.log(2 * Math.max(1, Math.abs(index))) / Math.log(3)) : 0);
    final byte[] indexTrits = new byte[length];

    Converter.copy(index, indexTrits, 0, length);

    final byte[] subseedPreimage = new byte[Curl_729_27.HASH_LENGTH];
    Adder.add(seed, indexTrits, subseedPreimage);

    final Curl_729_27 curl = new Curl_729_27(subseedPreimage.length);
    curl.absorb(subseedPreimage, 0, subseedPreimage.length);
    curl.squeeze(subseedTrits, 0, Curl_729_27.HASH_LENGTH);
  }

  public static void key(final byte[] subseedTrits, final int security, final byte[] keyTrits) {
    final Curl_729_27 curl = new Curl_729_27(Curl_729_27.HASH_LENGTH);
    curl.absorb(subseedTrits, 0, Curl_729_27.HASH_LENGTH);
    curl.squeeze(keyTrits, 0, keyTrits.length);

    for (int offset = 0; offset < keyTrits.length; offset += Curl_729_27.HASH_LENGTH) {
      final byte[] lengthTrits = new byte[Curl_729_27.HASH_LENGTH];
      Converter.copy(Curl_729_27.HASH_LENGTH, lengthTrits, 0, lengthTrits.length);
      curl.reset(lengthTrits);
      curl.absorb(keyTrits, offset, Curl_729_27.HASH_LENGTH);
      curl.squeeze(keyTrits, offset, Curl_729_27.HASH_LENGTH);
    }
  }

  public static void digests(final byte[] keyTrits, final byte[] digestsTrits) {
    final Curl_729_27 curl = new Curl_729_27(0);

    for (int i = 0; i < keyTrits.length / KEY_SIGNATURE_FRAGMENT_LENGTH; i++) {
      final byte[] buffer =
        Arrays.copyOfRange(keyTrits, i * KEY_SIGNATURE_FRAGMENT_LENGTH, (i + 1) * KEY_SIGNATURE_FRAGMENT_LENGTH);

      for (int j = 0; j < KEY_SIGNATURE_FRAGMENT_LENGTH / Curl_729_27.HASH_LENGTH; j++) {
        for (int k = 0; k < MAX_TRYTE_VALUE - MIN_TRYTE_VALUE; k++) {
          final byte[] lengthTrits = new byte[Curl_729_27.HASH_LENGTH];
          Converter.copy(Curl_729_27.HASH_LENGTH, lengthTrits, 0, lengthTrits.length);
          curl.reset(lengthTrits);
          curl.absorb(buffer, j * Curl_729_27.HASH_LENGTH, Curl_729_27.HASH_LENGTH);
          curl.squeeze(buffer, j * Curl_729_27.HASH_LENGTH, Curl_729_27.HASH_LENGTH);
        }
      }

      final byte[] lengthTrits = new byte[Curl_729_27.HASH_LENGTH];
      Converter.copy(KEY_SIGNATURE_FRAGMENT_LENGTH, lengthTrits, 0, lengthTrits.length);
      curl.reset(lengthTrits);
      curl.absorb(buffer, 0, KEY_SIGNATURE_FRAGMENT_LENGTH);
      curl.squeeze(digestsTrits, i * Curl_729_27.HASH_LENGTH, Curl_729_27.HASH_LENGTH);
    }
  }

  public static void digest(byte [] bundle, final byte[] signatureFragmentTrits, final byte[] digestTrits) {
    final byte[] buffer = Arrays.copyOfRange(signatureFragmentTrits, 0, KEY_SIGNATURE_FRAGMENT_LENGTH);
    final Curl_729_27 curl = new Curl_729_27(0);

    for (int j = 0; j < KEY_SIGNATURE_FRAGMENT_LENGTH / Curl_729_27.HASH_LENGTH; j++) {
      for (int k = bundle[j] - MIN_TRYTE_VALUE; k-- > 0; ) {
        final byte[] lengthTrits = new byte[Curl_729_27.HASH_LENGTH];
        Converter.copy(Curl_729_27.HASH_LENGTH, lengthTrits, 0, lengthTrits.length);
        curl.reset(lengthTrits);
        curl.absorb(buffer, j * Curl_729_27.HASH_LENGTH, Curl_729_27.HASH_LENGTH);
        curl.squeeze(buffer, j * Curl_729_27.HASH_LENGTH, Curl_729_27.HASH_LENGTH);
      }
    }

    final byte[] lengthTrits = new byte[Curl_729_27.HASH_LENGTH];
    Converter.copy(KEY_SIGNATURE_FRAGMENT_LENGTH, lengthTrits, 0, lengthTrits.length);
    curl.reset(lengthTrits);
    curl.absorb(buffer, 0, KEY_SIGNATURE_FRAGMENT_LENGTH);
    curl.squeeze(digestTrits, 0, Curl_729_27.HASH_LENGTH);
  }

  public static void addressFromDigests(final byte[] digestsTrits, final byte[] addressTrits) {
    final Curl_729_27 curl = new Curl_729_27(digestsTrits.length);

    curl.absorb(Arrays.copyOfRange(digestsTrits, 0, digestsTrits.length), 0, digestsTrits.length);
    curl.squeeze(addressTrits, 0, Curl_729_27.HASH_LENGTH);
  }


  public static void signatureFragment(final byte[] bundle, final byte[] keyFragment, final byte[] signatureFragmentTrits) {
    final Curl_729_27 curl = new Curl_729_27(0);
    final byte[] buffer = Arrays.copyOfRange(keyFragment, 0, KEY_SIGNATURE_FRAGMENT_LENGTH);

    for (int j = 0; j < KEY_SIGNATURE_FRAGMENT_LENGTH / Curl_729_27.HASH_LENGTH; j++) {
      for (int k = 0; k < MAX_TRYTE_VALUE - bundle[j]; k++) {
        final byte[] lengthTrits = new byte[Curl_729_27.HASH_LENGTH];
        Converter.copy(Curl_729_27.HASH_LENGTH, lengthTrits, 0, lengthTrits.length);
        curl.reset(lengthTrits);
        curl.absorb(buffer, j * Curl_729_27.HASH_LENGTH, Curl_729_27.HASH_LENGTH);
        curl.squeeze(buffer, j * Curl_729_27.HASH_LENGTH, Curl_729_27.HASH_LENGTH);
      }
    }

    for (int i = 0; i < KEY_SIGNATURE_FRAGMENT_LENGTH; i++) {
      signatureFragmentTrits[i] = buffer[i];
    }
  }

  public static boolean validateSignatures(final byte[] expectedAddress, final byte[][] signatureFragments, final byte[] bundle) {
    final byte[][] bundleFragments = new byte[signatureFragments.length][BUNDLE_FRAGMENT_LENGTH];

    for (int i = 0; i < NUMBER_OF_SECURITY_LEVELS; i++) {
      bundleFragments[i] = Arrays.copyOfRange(bundle, i * BUNDLE_FRAGMENT_TRYTE_LENGTH, (i + 1) * BUNDLE_FRAGMENT_TRYTE_LENGTH);
    }

    final byte[] digestsTrits = new byte [signatureFragments.length * Curl_729_27.HASH_LENGTH];

    for (int i = 0; i < signatureFragments.length; i++) {
        final byte[] buffer = new byte[Curl_729_27.HASH_LENGTH];

        digest(bundleFragments[i % NUMBER_OF_SECURITY_LEVELS], signatureFragments[i], buffer);

      for (int j = 0; j < Curl_729_27.HASH_LENGTH; j++) {
        digestsTrits[i * Curl_729_27.HASH_LENGTH + j] = buffer[j];
      }
    }

    final byte[] actualAddress = new byte[Curl_729_27.HASH_LENGTH];
    addressFromDigests(digestsTrits, actualAddress);

    for (int i = 0; i < actualAddress.length; i++) {
      if (actualAddress[i] != expectedAddress[i]) {
        return false;
      }
    }
    return true;
  }

  public static void getMerkleRoot(final byte[] hash, final byte[] trits, int index, final int depth) {
    final Curl_729_27 curl = new Curl_729_27(0);

    for (int i = 0; i < depth; i++) {

      final byte[] lengthTrits = new byte[Curl_729_27.HASH_LENGTH];
      Converter.copy(Curl_729_27.HASH_LENGTH, lengthTrits, 0, lengthTrits.length);
      curl.reset(lengthTrits);
      if ((index & 1) == 0) {
        curl.absorb(hash, 0, Curl_729_27.HASH_LENGTH);
        curl.absorb(trits, i * Curl_729_27.HASH_LENGTH, Curl_729_27.HASH_LENGTH);
      } else {
        curl.absorb(trits, i * Curl_729_27.HASH_LENGTH, Curl_729_27.HASH_LENGTH);
        curl.absorb(hash, 0, Curl_729_27.HASH_LENGTH);
      }
      curl.squeeze(hash, 0, Curl_729_27.HASH_LENGTH);

      index >>= 1;
    }
  }

}
