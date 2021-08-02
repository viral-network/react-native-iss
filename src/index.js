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

import {
  bundleTrytes,
  HASH_LENGTH,
  NUMBER_OF_SECURITY_LEVELS,
  MAX_TRYTE_VALUE,
  MIN_TRYTE_VALUE,
  BUNDLE_FRAGMENT_LENGTH,
  BUNDLE_FRAGMENT_TRYTE_LENGTH,
  KEY_SIGNATURE_FRAGMENT_LENGTH,
  SECURITY_LEVEL_TRITS,
  SECURITY_LEVEL_OFFSET,
} from '@web-ict/iss';

import { NativeModules } from 'react-native';

const { ReactNativeIss } = NativeModules;

export {
  bundleTrytes,
  HASH_LENGTH,
  NUMBER_OF_SECURITY_LEVELS,
  MAX_TRYTE_VALUE,
  MIN_TRYTE_VALUE,
  BUNDLE_FRAGMENT_LENGTH,
  BUNDLE_FRAGMENT_TRYTE_LENGTH,
  KEY_SIGNATURE_FRAGMENT_LENGTH,
  SECURITY_LEVEL_TRITS,
  SECURITY_LEVEL_OFFSET,
};

export const subseed = (seed, index) =>
  ReactNativeIss.subseed(Array.from(seed), index);

export const key = (subseedTrits, security) =>
  ReactNativeIss.key(Array.from(subseedTrits), security);

export const digests = (keyTrits) =>
  ReactNativeIss.digests(Array.from(keyTrits));

export const addressFromDigests = (digestsTrits) =>
  ReactNativeIss.addressFromDigests(Array.from(digestsTrits));

export const address =
  (increment) =>
  async (seed, security, digestsTrits = new Int8Array(0)) => {
    const outcome = {
      index: 0,
      security,
      digests: new Int8Array(digestsTrits.length + security * HASH_LENGTH),
      address: new Int8Array(HASH_LENGTH),
    };

    do {
      const index = await increment();
      outcome.index = index;
      outcome.digests.set(digestsTrits);
      outcome.digests.set(
        await digests(await key(await subseed(seed, index), security)),
        digestsTrits.length
      );

      outcome.address = await addressFromDigests(outcome.digests);
    } while (
      outcome.address[SECURITY_LEVEL_OFFSET] !== SECURITY_LEVEL_TRITS[security]
    );

    return outcome;
  };

export const digest = (bundle, signatureFragmentTrits) =>
  ReactNativeIss.digest(Array.from(bundle), Array.from(signatureFragmentTrits));

export const signatureFragment = (bundle, keyFragment) =>
  ReactNativeIss.signatureFragment(Array.from(bundle), Array.from(keyFragment));

export const validateSignatures = (
  expectedAddress,
  signatureFragments,
  bundle
) =>
  ReactNativeIss.validateSignatures(
    Array.from(expectedAddress),
    Array.from(signatureFragments),
    Array.from(bundle)
  );

export const getMerkleRoot = (hash, trits, index, depth) =>
  ReactNativeIss.getMerkleRoot(
    Array.from(hash),
    Array.from(trits),
    index,
    depth
  );

export const getMerkleProof = (root, index) => {
  const leaves = [];
  let node = root;
  let size = root.size;
  let leafIndex;

  if (index < size) {
    while (node !== undefined) {
      if (node.left === undefined) {
        leafIndex = node.leafIndex;
        break;
      }

      size = node.left.size;
      if (index < size) {
        leaves.push(node.right ? node.right : node.left);
        node = node.left;
      } else {
        leaves.push(node.left);
        node = node.right;
        index -= size;
      }
    }
  }

  leaves.reverse();

  const siblings = new Int8Array(leaves.length * HASH_LENGTH);
  for (let i = 0; i < leaves.length; i++) {
    siblings.set(leaves[i].address, i * HASH_LENGTH);
  }

  return {
    leafIndex,
    siblings,
  };
};

export const merkleTree = (increment) => async (seed, depth, security) => {
  const count = 2 ** depth;
  const start = await increment(count);
  const tree = await ReactNativeIss.merkleTree(seed, depth, start, security);
  return JSON.parse(tree);
};

export default (increment) => ({
  subseed,
  key,
  digests,
  address: address(increment),
  addressFromDigests,
  digest,
  signatureFragment,
  validateSignatures,
  bundleTrytes,
  getMerkleRoot,
  getMerkleProof,
  merkleTree: merkleTree(increment),
});
