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

const NUMBER_OF_ROUNDS = 27;
const LUT_0 = [
  1, 0, 0, 0, 1, 2, 2, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 1, 2, 0, 1, 2, 1, 0, 0, 1,
  2, 0, 0, 0, 0, 0, 2, 1, 2, 0, 0, 0, 1, 0, 0, 2, 0,
];
const LUT_1 = [
  1, 0, 2, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 0, 0, 0, 1, 1, 0, 0, 2, 2, 0, 0, 2, 1,
  1, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 1, 2, 0, 1, 2, 0,
];
const LUT_2 = [
  1, 1, 2, 0, 0, 1, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 1, 2, 1, 0, 1, 0,
  2, 0, 0, 0, 0, 0, 0, 2, 1, 0, 2, 1, 2, 0, 2, 1, 0,
];

const HASH_LENGTH = 243;
const STATE_LENGTH = 3 * HASH_LENGTH;

export class Curl729_27 {
  constructor(length) {
    this.state = new Int8Array(STATE_LENGTH);
    this.scratchpad = new Int8Array(STATE_LENGTH);

    this.reset(length);
    return this;
  }

  reset(length) {
    let length_trits = new Int8Array(HASH_LENGTH);
    let value_copy = length;
    let i = 0;

    while (value_copy > 0) {
      let remainder = Math.floor(value_copy % 3);
      value_copy /= 3;

      if (remainder > 1) {
        remainder = -1;
        value_copy += 1;
      }

      length_trits[i] = remainder;
      i += 1;
    }

    for (let j = 0; j < HASH_LENGTH; j++) {
      this.state[j] = (j + 1) % 3;
      this.state[HASH_LENGTH + j] = (length_trits[j] + 1) % 3;
      this.state[HASH_LENGTH * 2 + j] = (j + 1) % 3;
    }
  }

  static get_digest(
    message,
    message_offset,
    message_length,
    digest,
    digest_offset
  ) {
    let curl = new Curl729_27(message_length);
    curl.absorb(message, message_offset, message_length);

    for (let i = 0; i < HASH_LENGTH; i++) {
      digest[digest_offset + i] = curl.state[i] - 1;
    }
  }

  absorb(trits, offset, length) {
    let j = offset;
    let l = length;

    do {
      for (let i = 0; i < (l < HASH_LENGTH ? l : HASH_LENGTH); i++) {
        this.state[i] = trits[j] + 1;
        j += 1;
      }

      this.transform();

      l -= l < HASH_LENGTH ? l : HASH_LENGTH;
    } while (l > 0);
  }

  squeeze(trits, offset, length) {
    let l = length;
    let j = offset;

    do {
      for (let i = 0; i < (l < HASH_LENGTH ? l : HASH_LENGTH); i++) {
        trits[j] = this.state[i] - 1;
        j += 1;
      }

      this.transform();

      l -= l < HASH_LENGTH ? l : HASH_LENGTH;
    } while (l > 0);
  }

  transform() {
    let i = 0;
    while (i < NUMBER_OF_ROUNDS) {
      let a = 0;
      while (a < HASH_LENGTH) {
        let b = a + 243;
        let c = a + 486;
        // eslint-disable-next-line no-bitwise
        let index = this.state[a] | (this.state[b] << 2) | (this.state[c] << 4);
        this.scratchpad[a] = LUT_0[index];
        this.scratchpad[b] = LUT_1[index];
        this.scratchpad[c] = LUT_2[index];
        a += 1;
      }

      let j = 81;
      a = 0;
      while (a < STATE_LENGTH) {
        let b = a + 81;
        let c = a + 162;
        let index =
          // eslint-disable-next-line no-bitwise
          this.scratchpad[a] |
          // eslint-disable-next-line no-bitwise
          (this.scratchpad[b] << 2) |
          // eslint-disable-next-line no-bitwise
          (this.scratchpad[c] << 4);
        this.state[a] = LUT_0[index];
        this.state[b] = LUT_1[index];
        this.state[c] = LUT_2[index];
        j -= 1;
        if (j === 0) {
          a = c;
          j = 81;
        }
        a += 1;
      }

      a = 0;
      j = 27;
      while (a < STATE_LENGTH) {
        let b = a + 27;
        let c = a + 54;
        // eslint-disable-next-line no-bitwise
        let index = this.state[a] | (this.state[b] << 2) | (this.state[c] << 4);
        this.scratchpad[a] = LUT_0[index];
        this.scratchpad[b] = LUT_1[index];
        this.scratchpad[c] = LUT_2[index];
        j -= 1;
        if (j === 0) {
          a = c;
          j = 27;
        }
        a += 1;
      }

      a = 0;
      j = 9;
      while (a < STATE_LENGTH) {
        let b = a + 9;
        let c = a + 18;
        let index =
          // eslint-disable-next-line no-bitwise
          this.scratchpad[a] |
          // eslint-disable-next-line no-bitwise
          (this.scratchpad[b] << 2) |
          // eslint-disable-next-line no-bitwise
          (this.scratchpad[c] << 4);
        this.state[a] = LUT_0[index];
        this.state[b] = LUT_1[index];
        this.state[c] = LUT_2[index];
        j -= 1;
        if (j === 0) {
          a = c;
          j = 9;
        }
        a += 1;
      }

      a = 0;
      j = 3;
      while (a < STATE_LENGTH) {
        let b = a + 3;
        let c = a + 6;
        // eslint-disable-next-line no-bitwise
        let index = this.state[a] | (this.state[b] << 2) | (this.state[c] << 4);
        this.scratchpad[a] = LUT_0[index];
        this.scratchpad[b] = LUT_1[index];
        this.scratchpad[c] = LUT_2[index];
        j -= 1;
        if (j === 0) {
          a = c;
          j = 3;
        }
        a += 1;
      }

      a = 0;
      while (a < STATE_LENGTH) {
        let index =
          // eslint-disable-next-line no-bitwise
          this.scratchpad[a] |
          // eslint-disable-next-line no-bitwise
          (this.scratchpad[a + 1] << 2) |
          // eslint-disable-next-line no-bitwise
          (this.scratchpad[a + 2] << 4);
        this.state[a] = LUT_0[index];
        this.state[a + 1] = LUT_1[index];
        this.state[a + 2] = LUT_2[index];
        a += 3;
      }

      i += 1;
    }
  }
}
