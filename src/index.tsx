import { NativeModules } from 'react-native';

type ReactNativeIssType = {
  multiply(a: number, b: number): Promise<number>;
};

const { ReactNativeIss } = NativeModules;

export default ReactNativeIss as ReactNativeIssType;
