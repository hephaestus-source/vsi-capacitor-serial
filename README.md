# capacitor-serial

Serial port communication

## Install

```bash
npm install vsi-capacitor-serial
npx cap sync
```

## API

<docgen-index>

* [`getDevices()`](#getdevices)
* [`selectDevice(...)`](#selectdevice)
* [`requestPort(...)`](#requestport)
* [`open(...)`](#open)
* [`close()`](#close)
* [`write(...)`](#write)
* [`addListener('connected', ...)`](#addlistenerconnected-)
* [`addListener('disconnected', ...)`](#addlistenerdisconnected-)
* [`addListener('read', ...)`](#addlistenerread-)
* [Interfaces](#interfaces)
* [Type Aliases](#type-aliases)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### getDevices()

```typescript
getDevices() => Promise<ISerialDevices>
```

**Returns:** <code>Promise&lt;<a href="#iserialdevices">ISerialDevices</a>&gt;</code>

--------------------


### selectDevice(...)

```typescript
selectDevice(deviceId: number) => Promise<void>
```

| Param          | Type                |
| -------------- | ------------------- |
| **`deviceId`** | <code>number</code> |

--------------------


### requestPort(...)

```typescript
requestPort(params?: SerialPortRequestOptions | undefined) => Promise<ISerialPort>
```

| Param        | Type                                                                          |
| ------------ | ----------------------------------------------------------------------------- |
| **`params`** | <code><a href="#serialportrequestoptions">SerialPortRequestOptions</a></code> |

**Returns:** <code>Promise&lt;<a href="#iserialport">ISerialPort</a>&gt;</code>

--------------------


### open(...)

```typescript
open(params: SerialOptions) => Promise<void>
```

| Param        | Type                                                    |
| ------------ | ------------------------------------------------------- |
| **`params`** | <code><a href="#serialoptions">SerialOptions</a></code> |

--------------------


### close()

```typescript
close() => Promise<void>
```

--------------------


### write(...)

```typescript
write(data: SerialPortData) => Promise<void>
```

| Param      | Type                                                      |
| ---------- | --------------------------------------------------------- |
| **`data`** | <code><a href="#serialportdata">SerialPortData</a></code> |

--------------------


### addListener('connected', ...)

```typescript
addListener(eventName: 'connected', listenerFunc: (response: ISerialConnectionEvent) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                                             |
| ------------------ | ------------------------------------------------------------------------------------------------ |
| **`eventName`**    | <code>'connected'</code>                                                                         |
| **`listenerFunc`** | <code>(response: <a href="#iserialconnectionevent">ISerialConnectionEvent</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('disconnected', ...)

```typescript
addListener(eventName: 'disconnected', listenerFunc: (response: ISerialConnectionEvent) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                                             |
| ------------------ | ------------------------------------------------------------------------------------------------ |
| **`eventName`**    | <code>'disconnected'</code>                                                                      |
| **`listenerFunc`** | <code>(response: <a href="#iserialconnectionevent">ISerialConnectionEvent</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### addListener('read', ...)

```typescript
addListener(eventName: 'read', listenerFunc: (response: ISerialReadDataEvent) => void) => Promise<PluginListenerHandle>
```

| Param              | Type                                                                                         |
| ------------------ | -------------------------------------------------------------------------------------------- |
| **`eventName`**    | <code>'read'</code>                                                                          |
| **`listenerFunc`** | <code>(response: <a href="#iserialreaddataevent">ISerialReadDataEvent</a>) =&gt; void</code> |

**Returns:** <code>Promise&lt;<a href="#pluginlistenerhandle">PluginListenerHandle</a>&gt;</code>

--------------------


### Interfaces


#### ISerialDevices

| Prop       | Type                       |
| ---------- | -------------------------- |
| **`list`** | <code>ISerialPort[]</code> |


#### ISerialPort

| Prop                   | Type                |
| ---------------------- | ------------------- |
| **`portId`**           | <code>number</code> |
| **`portName`**         | <code>string</code> |
| **`deviceInstanceId`** | <code>number</code> |
| **`displayName`**      | <code>string</code> |
| **`productId`**        | <code>number</code> |
| **`vendorId`**         | <code>number</code> |
| **`usbDriverName`**    | <code>string</code> |


#### SerialPortRequestOptions

| Prop                                  | Type                              | Description                                                                                                                                                                                                                                                                                                              |
| ------------------------------------- | --------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| **`filters`**                         | <code>SerialPortFilter[]</code>   |                                                                                                                                                                                                                                                                                                                          |
| **`allowedBluetoothServiceClassIds`** | <code>(string \| number)[]</code> | A list of BluetoothServiceUUID values representing Bluetooth service class IDs. Bluetooth ports with custom service class IDs are excluded from the list of ports presented to the user unless the service class ID is included in this list. {@link https://wicg.github.io/serial/#serialportrequestoptions-dictionary} |


#### SerialPortFilter

| Prop                          | Type                          |
| ----------------------------- | ----------------------------- |
| **`usbVendorId`**             | <code>number</code>           |
| **`usbProductId`**            | <code>number</code>           |
| **`bluetoothServiceClassId`** | <code>string \| number</code> |


#### Array

| Prop         | Type                | Description                                                                                            |
| ------------ | ------------------- | ------------------------------------------------------------------------------------------------------ |
| **`length`** | <code>number</code> | Gets or sets the length of the array. This is a number one higher than the highest index in the array. |

| Method             | Signature                                                                                                                     | Description                                                                                                                                                                                                                                 |
| ------------------ | ----------------------------------------------------------------------------------------------------------------------------- | ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| **toString**       | () =&gt; string                                                                                                               | Returns a string representation of an array.                                                                                                                                                                                                |
| **toLocaleString** | () =&gt; string                                                                                                               | Returns a string representation of an array. The elements are converted to string using their toLocalString methods.                                                                                                                        |
| **pop**            | () =&gt; T \| undefined                                                                                                       | Removes the last element from an array and returns it. If the array is empty, undefined is returned and the array is not modified.                                                                                                          |
| **push**           | (...items: T[]) =&gt; number                                                                                                  | Appends new elements to the end of an array, and returns the new length of the array.                                                                                                                                                       |
| **concat**         | (...items: <a href="#concatarray">ConcatArray</a>&lt;T&gt;[]) =&gt; T[]                                                       | Combines two or more arrays. This method returns a new array without modifying any existing arrays.                                                                                                                                         |
| **concat**         | (...items: (T \| <a href="#concatarray">ConcatArray</a>&lt;T&gt;)[]) =&gt; T[]                                                | Combines two or more arrays. This method returns a new array without modifying any existing arrays.                                                                                                                                         |
| **join**           | (separator?: string \| undefined) =&gt; string                                                                                | Adds all the elements of an array into a string, separated by the specified separator string.                                                                                                                                               |
| **reverse**        | () =&gt; T[]                                                                                                                  | Reverses the elements in an array in place. This method mutates the array and returns a reference to the same array.                                                                                                                        |
| **shift**          | () =&gt; T \| undefined                                                                                                       | Removes the first element from an array and returns it. If the array is empty, undefined is returned and the array is not modified.                                                                                                         |
| **slice**          | (start?: number \| undefined, end?: number \| undefined) =&gt; T[]                                                            | Returns a copy of a section of an array. For both start and end, a negative index can be used to indicate an offset from the end of the array. For example, -2 refers to the second to last element of the array.                           |
| **sort**           | (compareFn?: ((a: T, b: T) =&gt; number) \| undefined) =&gt; this                                                             | Sorts an array in place. This method mutates the array and returns a reference to the same array.                                                                                                                                           |
| **splice**         | (start: number, deleteCount?: number \| undefined) =&gt; T[]                                                                  | Removes elements from an array and, if necessary, inserts new elements in their place, returning the deleted elements.                                                                                                                      |
| **splice**         | (start: number, deleteCount: number, ...items: T[]) =&gt; T[]                                                                 | Removes elements from an array and, if necessary, inserts new elements in their place, returning the deleted elements.                                                                                                                      |
| **unshift**        | (...items: T[]) =&gt; number                                                                                                  | Inserts new elements at the start of an array, and returns the new length of the array.                                                                                                                                                     |
| **indexOf**        | (searchElement: T, fromIndex?: number \| undefined) =&gt; number                                                              | Returns the index of the first occurrence of a value in an array, or -1 if it is not present.                                                                                                                                               |
| **lastIndexOf**    | (searchElement: T, fromIndex?: number \| undefined) =&gt; number                                                              | Returns the index of the last occurrence of a specified value in an array, or -1 if it is not present.                                                                                                                                      |
| **every**          | &lt;S extends T&gt;(predicate: (value: T, index: number, array: T[]) =&gt; value is S, thisArg?: any) =&gt; this is S[]       | Determines whether all the members of an array satisfy the specified test.                                                                                                                                                                  |
| **every**          | (predicate: (value: T, index: number, array: T[]) =&gt; unknown, thisArg?: any) =&gt; boolean                                 | Determines whether all the members of an array satisfy the specified test.                                                                                                                                                                  |
| **some**           | (predicate: (value: T, index: number, array: T[]) =&gt; unknown, thisArg?: any) =&gt; boolean                                 | Determines whether the specified callback function returns true for any element of an array.                                                                                                                                                |
| **forEach**        | (callbackfn: (value: T, index: number, array: T[]) =&gt; void, thisArg?: any) =&gt; void                                      | Performs the specified action for each element in an array.                                                                                                                                                                                 |
| **map**            | &lt;U&gt;(callbackfn: (value: T, index: number, array: T[]) =&gt; U, thisArg?: any) =&gt; U[]                                 | Calls a defined callback function on each element of an array, and returns an array that contains the results.                                                                                                                              |
| **filter**         | &lt;S extends T&gt;(predicate: (value: T, index: number, array: T[]) =&gt; value is S, thisArg?: any) =&gt; S[]               | Returns the elements of an array that meet the condition specified in a callback function.                                                                                                                                                  |
| **filter**         | (predicate: (value: T, index: number, array: T[]) =&gt; unknown, thisArg?: any) =&gt; T[]                                     | Returns the elements of an array that meet the condition specified in a callback function.                                                                                                                                                  |
| **reduce**         | (callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) =&gt; T) =&gt; T                           | Calls the specified callback function for all the elements in an array. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function.                      |
| **reduce**         | (callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) =&gt; T, initialValue: T) =&gt; T          |                                                                                                                                                                                                                                             |
| **reduce**         | &lt;U&gt;(callbackfn: (previousValue: U, currentValue: T, currentIndex: number, array: T[]) =&gt; U, initialValue: U) =&gt; U | Calls the specified callback function for all the elements in an array. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function.                      |
| **reduceRight**    | (callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) =&gt; T) =&gt; T                           | Calls the specified callback function for all the elements in an array, in descending order. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function. |
| **reduceRight**    | (callbackfn: (previousValue: T, currentValue: T, currentIndex: number, array: T[]) =&gt; T, initialValue: T) =&gt; T          |                                                                                                                                                                                                                                             |
| **reduceRight**    | &lt;U&gt;(callbackfn: (previousValue: U, currentValue: T, currentIndex: number, array: T[]) =&gt; U, initialValue: U) =&gt; U | Calls the specified callback function for all the elements in an array, in descending order. The return value of the callback function is the accumulated result, and is provided as an argument in the next call to the callback function. |


#### ConcatArray

| Prop         | Type                |
| ------------ | ------------------- |
| **`length`** | <code>number</code> |

| Method    | Signature                                                          |
| --------- | ------------------------------------------------------------------ |
| **join**  | (separator?: string \| undefined) =&gt; string                     |
| **slice** | (start?: number \| undefined, end?: number \| undefined) =&gt; T[] |


#### SerialOptions

| Prop              | Type                                                        |
| ----------------- | ----------------------------------------------------------- |
| **`baudRate`**    | <code>number</code>                                         |
| **`dataBits`**    | <code>number</code>                                         |
| **`stopBits`**    | <code>number</code>                                         |
| **`parity`**      | <code><a href="#paritytype">ParityType</a></code>           |
| **`bufferSize`**  | <code>number</code>                                         |
| **`flowControl`** | <code><a href="#flowcontroltype">FlowControlType</a></code> |


#### SerialPortData

| Prop       | Type                |
| ---------- | ------------------- |
| **`data`** | <code>string</code> |


#### PluginListenerHandle

| Prop         | Type                                      |
| ------------ | ----------------------------------------- |
| **`remove`** | <code>() =&gt; Promise&lt;void&gt;</code> |


#### ISerialConnectionEvent

| Prop             | Type                                                          |
| ---------------- | ------------------------------------------------------------- |
| **`action`**     | <code><a href="#connectionaction">ConnectionAction</a></code> |
| **`deviceName`** | <code>string</code>                                           |


#### ISerialReadDataEvent

| Prop       | Type                |
| ---------- | ------------------- |
| **`data`** | <code>string</code> |


### Type Aliases


#### ParityType

<code>"none" | "even" | "odd"</code>


#### FlowControlType

<code>"none" | "hardware"</code>


#### ConnectionAction

<code>'DISCONNECTED' | 'CONNECTED'</code>

</docgen-api>
