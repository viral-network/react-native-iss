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

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.module.annotations.ReactModule;

@ReactModule(name = ReactNativeIssModule.NAME)
public class ReactNativeIssModule extends ReactContextBaseJavaModule {
    public static final String NAME = "ReactNativeIss";

    public ReactNativeIssModule(ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Override
    @NonNull
    public String getName() {
        return NAME;
    }

    @ReactMethod
    public void subseed(final ReadableArray seedTrits, final int index, Promise promise) {
        final byte[] seedTritsCopy = new byte[Curl_729_27.HASH_LENGTH];
        for (int i = 0; i < Curl_729_27.HASH_LENGTH; i++) {
            seedTritsCopy[i] = (byte) seedTrits.getInt(i);
        }
        final byte[] subseedTrits = new byte[Curl_729_27.HASH_LENGTH];
        ISS.subseed(seedTritsCopy, index, subseedTrits);

        final WritableArray subseedTritsCopy = Arguments.createArray();
        for (int i = 0; i < subseedTrits.length; i++) {
            subseedTritsCopy.pushInt((int) subseedTrits[i]);
        }
        promise.resolve(subseedTritsCopy);
    }

    @ReactMethod
    public void key(final ReadableArray subseedTrits, final int security, Promise promise) {
        final byte[] subseedTritsCopy = new byte[Curl_729_27.HASH_LENGTH];
        for (int i = 0; i < Curl_729_27.HASH_LENGTH; i++) {
            subseedTritsCopy[i] = (byte) subseedTrits.getInt(i);
        }
        final byte[] keyTrits = new byte[security * ISS.KEY_SIGNATURE_FRAGMENT_LENGTH];
        ISS.key(subseedTritsCopy, security, keyTrits);

        final WritableArray keyTritsCopy = Arguments.createArray();
        for (int i = 0; i < keyTrits.length; i++) {
            keyTritsCopy.pushInt((int) keyTrits[i]);
        }
        promise.resolve(keyTritsCopy);
    }

    @ReactMethod
    public void digests(final ReadableArray keyTrits, Promise promise) {
        final byte[] keyTritsCopy = new byte[keyTrits.size()];
        for (int i = 0; i < keyTritsCopy.length; i++) {
            keyTritsCopy[i] = (byte) keyTrits.getInt(i);
        }
        final byte[] digestsTrits = new byte[(keyTritsCopy.length / ISS.KEY_SIGNATURE_FRAGMENT_LENGTH) * Curl_729_27.HASH_LENGTH];
        ISS.digests(keyTritsCopy, digestsTrits);

        final WritableArray digestsTritsCopy = Arguments.createArray();
        for (int i = 0; i < digestsTrits.length; i++) {
            digestsTritsCopy.pushInt((int) digestsTrits[i]);
        }
        promise.resolve(digestsTritsCopy);
    }

    @ReactMethod
    public void digest(final ReadableArray bundle, final ReadableArray signatureFragmentTrits, Promise promise) {
        final byte[] bundleCopy = new byte[bundle.size()];
        final byte[] signatureFragmentTritsCopy = new byte[signatureFragmentTrits.size()];
        for (int i = 0; i < bundleCopy.length; i++) {
            bundleCopy[i] = (byte) bundle.getInt(i);
        }
        for (int i = 0; i < signatureFragmentTritsCopy.length; i++) {
            signatureFragmentTritsCopy[i] = (byte) signatureFragmentTrits.getInt(i);
        }
        final byte[] digestTrits = new byte[Curl_729_27.HASH_LENGTH];
        ISS.digest(bundleCopy, signatureFragmentTritsCopy, digestTrits);

        final WritableArray digestTritsCopy = Arguments.createArray();
        for (int i = 0; i < digestTrits.length; i++) {
            digestTritsCopy.pushInt((int) digestTrits[i]);
        }
        promise.resolve(digestTritsCopy);
    }


    @ReactMethod
    public void addressFromDigests(final ReadableArray digestsTrits, Promise promise) {
        final byte[] digestsTritsCopy = new byte[digestsTrits.size()];
        for (int i = 0; i < digestsTritsCopy.length; i++) {
            digestsTritsCopy[i] = (byte) digestsTrits.getInt(i);
        }
        final byte[] addressTrits = new byte[Curl_729_27.HASH_LENGTH];
        ISS.addressFromDigests(digestsTritsCopy, addressTrits);

        final WritableArray addressTritsCopy = Arguments.createArray();
        for (int i = 0; i < addressTrits.length; i++) {
          addressTritsCopy.pushInt((int) addressTrits[i]);
        }
        promise.resolve(addressTritsCopy);
    }

    @ReactMethod
    public void signatureFragment(final ReadableArray bundle, final ReadableArray keyTrits, Promise promise) {
        final byte[] bundleCopy = new byte[bundle.size()];
        final byte[] keyTritsCopy = new byte[keyTrits.size()];
        for (int i = 0; i < bundleCopy.length; i++) {
            bundleCopy[i] = (byte) bundle.getInt(i);
        }
        for (int i = 0; i < keyTritsCopy.length; i++) {
            keyTritsCopy[i] = (byte) keyTrits.getInt(i);
        }
        final byte[] signatureFragmentTrits = new byte[ISS.KEY_SIGNATURE_FRAGMENT_LENGTH];
        ISS.signatureFragment(bundleCopy, keyTritsCopy, signatureFragmentTrits);

        final WritableArray signatureFragmentTritsCopy = Arguments.createArray();
        for (int i = 0; i < signatureFragmentTrits.length; i++) {
            signatureFragmentTritsCopy.pushInt((int) signatureFragmentTrits[i]);
        }
        promise.resolve(signatureFragmentTrits);
    }

    @ReactMethod
    public void validateSignature(final ReadableArray expectedAddress, final ReadableArray signatureFragments, final ReadableArray bundle, Promise promise) {
        final byte[] expectedAddressCopy = new byte[Curl_729_27.HASH_LENGTH];
        final byte[][] signatureFragmentsCopy = new byte[signatureFragments.size()][ISS.KEY_SIGNATURE_FRAGMENT_LENGTH];
        final byte[] bundleCopy = new byte[bundle.size()];
        for (int i = 0; i < expectedAddressCopy.length; i++) {
            expectedAddressCopy[i] = (byte) expectedAddress.getInt(i);
        }
        for (int i = 0; i < signatureFragmentsCopy.length; i++) {
            final ReadableArray buffer = signatureFragments.getArray(i);
            for (int j = 0; j < ISS.KEY_SIGNATURE_FRAGMENT_LENGTH; j++) {
                signatureFragmentsCopy[i][j] = (byte) buffer.getInt(j);
            }
        }
        for (int i = 0; i < bundleCopy.length; i++) {
          bundleCopy[i] = (byte) bundle.getInt(i);
        }

        promise.resolve(ISS.validateSignatures(expectedAddressCopy, signatureFragmentsCopy, bundleCopy));
    }

    @ReactMethod
    public void getMerkleRoot(final ReadableArray hash, final ReadableArray trits, final int index, final int depth, Promise promise) {
        final byte[] hashCopy = new byte[Curl_729_27.HASH_LENGTH];
        final byte[] tritsCopy = new byte[trits.size()];
        for (int i = 0; i < Curl_729_27.HASH_LENGTH; i++) {
            hashCopy[i] = (byte) hash.getInt(i);
        }
        for (int i = 0; i < tritsCopy.length; i++) {
            tritsCopy[i] = (byte) trits.getInt(i);
        }

        ISS.getMerkleRoot(hashCopy, tritsCopy, index, depth);

        final WritableArray hashCopy2 = Arguments.createArray();
        for (int i = 0; i < Curl_729_27.HASH_LENGTH; i++) {
            hashCopy2.pushInt(hashCopy[i]);
        }
        promise.resolve(hashCopy2);
    }
}
