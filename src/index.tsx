import { NativeModules, Platform } from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-in-app-updates-android' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

const InAppUpdatesAndroid = NativeModules.InAppUpdatesAndroid
  ? NativeModules.InAppUpdatesAndroid
  : new Proxy(
      {},
      {
        get() {
          throw new Error(LINKING_ERROR);
        },
      }
    );

export function multiply(a: number, b: number): Promise<number> {
  return InAppUpdatesAndroid.multiply(a, b);
}

export function checkForUpdate(options: {
  stalenessDays: number;
}): Promise<'update_exists' | 'update_downloaded' | 'no_updates_available'> {
  return InAppUpdatesAndroid.checkForUpdate(options);
}

export function startFlexibleUpdate(): Promise<void> {
  return InAppUpdatesAndroid.startFlexibleUpdate();
}

export function installFlexibleUpdate(): Promise<void> {
  return InAppUpdatesAndroid.installFlexibleUpdate();
}
