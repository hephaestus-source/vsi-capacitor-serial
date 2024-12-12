import { registerPlugin } from '@capacitor/core';

import type { SerialPlugin } from './definitions';

const Serial = registerPlugin<SerialPlugin>('Serial', {
  web: () => import('./web').then(({ SerialWeb }) => new SerialWeb())});

export * from './definitions';
export * from './web-serial'
export { Serial };
