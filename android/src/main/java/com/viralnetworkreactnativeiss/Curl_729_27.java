/*
(c) Come-from-Beyond

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

public class Curl_729_27 {

  static final int HASH_LENGTH = 243;
  private static final int STATE_LENGTH = 3 * HASH_LENGTH;
  private static final byte[] LUT_0 = {1, 0, 0, 0, 1, 2, 2, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 1, 2, 0, 1, 2, 1, 0, 0, 1, 2, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 1, 0, 0, 2, 0};
  private static final byte[] LUT_1 = {1, 0, 2, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 0, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 2, 1, 1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 2, 0, 1, 2, 0};
  private static final byte[] LUT_2 = {1, 1, 2, 0, 0, 1, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 1, 2, 1, 0, 1, 0, 2, 0, 0, 0, 0, 0, 0, 2, 1, 0, 2, 1, 2, 0, 2, 1, 0};
  private static final int NUMBER_OF_ROUNDS = 27;

  private final byte[] state = new byte[STATE_LENGTH];
  private final byte[] scratchpad = new byte[STATE_LENGTH];

  public Curl_729_27(final int length) {

    final byte[] lengthTrits = new byte[HASH_LENGTH];
    Converter.copy(length, lengthTrits, 0, lengthTrits.length);
    reset(lengthTrits);
  }

  public static void getDigest(final byte[] messageTrits, final int messageOffset, final int messageLength,
                               final byte[] digestTrits, int digestOffset) {

    final Curl_729_27 curl = new Curl_729_27(messageLength);
    curl.absorb(messageTrits, messageOffset, messageLength);
    for (int i = 0; i < HASH_LENGTH; i++) {

      digestTrits[digestOffset++] = (byte) (curl.state[i] - 1);
    }
  }

  public void reset(final byte[] lengthTrits) { // "lengthTrits" must contain the message length converted into HASH_LENGTH trits

    for (int i = 0; i < HASH_LENGTH; i++) {

      state[HASH_LENGTH * 0 + i] = (byte) ((i + 1) % 3);
      state[HASH_LENGTH * 1 + i] = (byte) ((lengthTrits[i] + 1) % 3);
      state[HASH_LENGTH * 2 + i] = (byte) ((i + 1) % 3);
    }
  }

  public void absorb(final byte[] trits, int offset, int length) {

    do {

      for (int i = 0; i < (length < HASH_LENGTH ? length : HASH_LENGTH); i++) {

        state[i] = (byte) (trits[offset++] + 1);
      }
      transform();

    } while ((length -= HASH_LENGTH) > 0);
  }

  public void squeeze(final byte[] trits, int offset, int length) {

    do {

      for (int i = 0; i < (length < HASH_LENGTH ? length : HASH_LENGTH); i++) {

        trits[offset++] = (byte) (state[i] - 1);
      }
      transform();

    } while ((length -= HASH_LENGTH) > 0);
  }

  private void transform() {

    for (int i = NUMBER_OF_ROUNDS; i-- > 0; ) {

      for (int a = 0; a < HASH_LENGTH; a++) {

        final int index, b, c;
        scratchpad[a] = LUT_0[index = state[a] | (state[b = a + 243] << 2) | (state[c = a + 486] << 4)];
        scratchpad[b] = LUT_1[index];
        scratchpad[c] = LUT_2[index];
      }
      for (int a = 0, j = 81; a < STATE_LENGTH; a++) {

        final int index, b, c;
        state[a] = LUT_0[index = scratchpad[a] | (scratchpad[b = a + 81] << 2) | (scratchpad[c = a + 162] << 4)];
        state[b] = LUT_1[index];
        state[c] = LUT_2[index];

        if (--j == 0) {

          j = 81;
          a = c;
        }
      }
      for (int a = 0, j = 27; a < STATE_LENGTH; a++) {

        final int index, b, c;
        scratchpad[a] = LUT_0[index = state[a] | (state[b = a + 27] << 2) | (state[c = a + 54] << 4)];
        scratchpad[b] = LUT_1[index];
        scratchpad[c] = LUT_2[index];

        if (--j == 0) {

          j = 27;
          a = c;
        }
      }
      for (int a = 0, j = 9; a < STATE_LENGTH; a++) {

        final int index, b, c;
        state[a] = LUT_0[index = scratchpad[a] | (scratchpad[b = a + 9] << 2) | (scratchpad[c = a + 18] << 4)];
        state[b] = LUT_1[index];
        state[c] = LUT_2[index];

        if (--j == 0) {

          j = 9;
          a = c;
        }
      }
      for (int a = 0, j = 3; a < STATE_LENGTH; a++) {

        final int index, b, c;
        scratchpad[a] = LUT_0[index = state[a] | (state[b = a + 3] << 2) | (state[c = a + 6] << 4)];
        scratchpad[b] = LUT_1[index];
        scratchpad[c] = LUT_2[index];

        if (--j == 0) {

          j = 3;
          a = c;
        }
      }
      for (int a = 0; a < STATE_LENGTH; a += 3) {

        final int index;
        state[a] = LUT_0[index = scratchpad[a] | (scratchpad[a + 1] << 2) | (scratchpad[a + 2] << 4)];
        state[a + 1] = LUT_1[index];
        state[a + 2] = LUT_2[index];
      }
    }
  }
}
