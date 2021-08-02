package com.viralnetworkreactnativeiss;

public class Node {
  byte[] address;
  int leafIndex;
  int size;
  Node left;
  Node right;

  public Node(final byte[] a, final int i, final int s, final Node l, final Node r) {
    address = a;
    leafIndex = i;
    size = s;
    left = l;
    right = r;
  }
}
