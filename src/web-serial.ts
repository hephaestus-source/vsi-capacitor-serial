import type { SerialPlugin } from './definitions';
import type { ISerialPort } from './index';

class W3CSerialPort {
  readonly connected: boolean;
  readable: ReadableStream<Uint8Array> | null = null;
  writable: WritableStream<Uint8Array> | null = null;

  onconnect: ((this: this, ev: Event) => any) | null = null;
  ondisconnect: ((this: this, ev: Event) => any) | null = null;

  connectEvents: ((this: this, ev: Event) => void)[] = [];
  disconnectEvents: ((this: this, ev: Event) => void)[] = [];

  constructor(
    private readonly serial: SerialPlugin,
    private readonly port: ISerialPort,
  ) {
    this.connected = true;
  }

  async open(options: SerialOptions): Promise<void> {
    await this.serial.open(options);

    await this.initStreams();
  }

  getInfo(): SerialPortInfo {
    return {
      usbVendorId: this.port.vendorId,
      usbProductId: this.port.productId,
    };
  }

  async close(): Promise<void> {
    await this.serial.close();
  }

  forget(): Promise<void> {
    return Promise.resolve();
  }

  addEventListener(type: 'connect' | 'disconnect', listener: (this: this, ev: Event) => void): void {
    if (type === 'connect') {
      this.connectEvents.push(listener);
    }

    if (type === 'disconnect') {
      this.disconnectEvents.push(listener);
    }
  }

  removeEventListener(type: 'connect' | 'disconnect', callback: (this: this, ev: Event) => void): void {
    const list = type === 'connect' ? this.connectEvents : this.disconnectEvents;
    const index = list.indexOf(callback);

    if (index == -1) {
      return;
    }

    list.splice(index, 1);
  }

  private async initStreams(): Promise<void> {
    this.writable = new WritableStream<Uint8Array>({
      write: async (chunk) => {
        const binaryString = Array.from(chunk)
          .map((byte) => String.fromCharCode(byte))
          .join('');
        const base64Data = btoa(binaryString);

        await this.serial.write({
          data: base64Data,
        });
      },
    });
    this.readable = new ReadableStream<Uint8Array>({
      start: async (controller) => {
        await this.serial.addListener('read', ({ data }) => {
          const binaryString = atob(data);
          const rawBytes = new Uint8Array(binaryString.length);

          for (let i = 0; i < binaryString.length; i++) {
            rawBytes[i] = binaryString.charCodeAt(i);
          }

          controller.enqueue(rawBytes);
        });
      },
    });

    await this.serial.addListener('connected', () =>
      this.connectEvents.forEach((cb) => cb.call(this, new Event('connected'))),
    );
    await this.serial.addListener('disconnected', () =>
      this.connectEvents.forEach((cb) => cb.call(this, new Event('disconnected'))),
    );
  }
}

export class W3CSerial {
  constructor(private readonly serial: SerialPlugin) {}

  private ports: Map<string, SerialPort> = new Map();

  onconnect: ((this: this, ev: Event) => any) | null = null;
  ondisconnect: ((this: this, ev: Event) => any) | null = null;

  getPorts(): Promise<SerialPort[]> {
    return Promise.resolve(Array.from(this.ports.values()));
  }

  async requestPort(options?: SerialPortRequestOptions): Promise<SerialPort> {
    const result = await this.serial.requestPort(options);

    return new W3CSerialPort(this.serial, result) as unknown as SerialPort;
  }
}
