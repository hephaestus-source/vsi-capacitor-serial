import { WebPlugin } from '@capacitor/core';
import type { PluginListenerHandle } from '@capacitor/core';
import type { ListenerCallback } from '@capacitor/core/types/web-plugin';

import type { ConnectionCallback, ISerialPort, SerialPlugin, SerialPortData, ISerialDevices } from './definitions';

export class SerialWeb extends WebPlugin implements SerialPlugin {
  private port: SerialPort | null = null;
  private connectionEvents: Set<ConnectionCallback> = new Set();

  async getDevices(): Promise<ISerialDevices> {
    const response = await navigator.serial.getPorts();

    return {
      list: response.map((item) => {
        const info = item.getInfo();

        return {
          deviceInstanceId: info.usbProductId,
          displayName: `${info.usbProductId} ${info.usbVendorId}`,
          vendorId: info.usbVendorId,
          productId: info.usbVendorId,
          portId: info.usbProductId,
          portName: `${info.usbProductId} ${info.usbVendorId}`,
          usbDriverName: '',
        } as ISerialPort;
      }),
    };
  }

  async requestPort(params?: SerialPortRequestOptions): Promise<ISerialPort> {
    this.port = await navigator.serial.requestPort(params);

    this.addEventListeners(this.port);

    return this.port as unknown as ISerialPort;
  }

  selectDevice(): Promise<void> {
    return Promise.resolve();
  }

  async open(deviceId: SerialOptions): Promise<void> {
    if (!this.port) {
      throw new Error('The port cannot be opened as it was not requested');
    }

    await this.port.open(deviceId);
  }

  async close(): Promise<void> {
    if (!this.port) {
      throw new Error('The port cannot be closed as it was is not requested');
    }

    await this.port.close();
  }

  async write(data: SerialPortData): Promise<void> {
    if (!this.port) {
      throw new Error('The port is not requested');
    }

    if (!this.port.writable) {
      throw new Error('The port is not opened to write');
    }

    const writer = this.port.writable.getWriter();

    const binaryString = atob(data.data);
    const rawBytes = new Uint8Array(binaryString.length);

    for (let i = 0; i < binaryString.length; i++) {
      rawBytes[i] = binaryString.charCodeAt(i);
    }

    await writer.write(rawBytes);

    writer.releaseLock();
  }

  async addListener(eventName: string, listenerFunc: ListenerCallback): Promise<PluginListenerHandle> {
    console.log('eventName', eventName, 'listenerFunc', listenerFunc);

    return {
      remove: () => Promise.resolve(void 0),
    };
  }

  private addEventListeners(port: SerialPort) {
    port.addEventListener('connect', () => {
      Array.from(this.connectionEvents.values()).forEach((callback) => {
        const info = port.getInfo();
        callback({
          action: 'CONNECTED',
          deviceName: `${info.usbVendorId} ${info.usbProductId}`,
        });
      });
    });

    port.addEventListener('disconnect', () => {
      Array.from(this.connectionEvents.values()).forEach((callback) => {
        const info = port.getInfo();
        callback({
          action: 'DISCONNECTED',
          deviceName: `${info.usbVendorId} ${info.usbProductId}`,
        });
      });
    });
  }
}

export const serialWeb = new SerialWeb();
