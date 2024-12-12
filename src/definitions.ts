import type { PluginListenerHandle } from '@capacitor/core';

export interface ISerialPort {
  portId: number;
  portName: string;
  deviceInstanceId: number;
  displayName: string;
  productId: number;
  vendorId: number;
  usbDriverName: string;
}

export interface ISerialDevices {
  list: ISerialPort[];
}

export type ConnectionAction = 'DISCONNECTED' | 'CONNECTED';

export interface ISerialConnectionEvent {
  action: ConnectionAction;
  deviceName: string;
}

export interface ISerialReadDataEvent {
  data: string;
}

export type ConnectionCallback = (params: ISerialConnectionEvent) => void;

export interface SerialPortData {
  data: string;
}

export interface SerialPlugin {
  getDevices(): Promise<ISerialDevices>;
  selectDevice(deviceId: number): Promise<void>;
  requestPort(params?: SerialPortRequestOptions): Promise<ISerialPort>;
  open(params: SerialOptions): Promise<void>;
  close(): Promise<void>;
  write(data: SerialPortData): Promise<void>;
  addListener(
    eventName: 'connected',
    listenerFunc: (response: ISerialConnectionEvent) => void,
  ): Promise<PluginListenerHandle>;
  addListener(
    eventName: 'disconnected',
    listenerFunc: (response: ISerialConnectionEvent) => void,
  ): Promise<PluginListenerHandle>;
  addListener(eventName: 'read', listenerFunc: (response: ISerialReadDataEvent) => void): Promise<PluginListenerHandle>;
}
