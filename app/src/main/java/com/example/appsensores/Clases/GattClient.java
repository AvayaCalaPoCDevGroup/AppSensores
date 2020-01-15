package com.example.appsensores.Clases;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.audiofx.AudioEffect;
import android.util.Log;
import android.widget.Toast;

import com.example.appsensores.Models.Dispositivos.DispoThunderBoard;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.content.Context.BLUETOOTH_SERVICE;

public class GattClient {

    private static final String TAG = GattClient.class.getSimpleName();
    private int nextDescriptor = 0;
    private int nextCharacteristic = 0;
    private ArrayList<BluetoothGattDescriptor> listDescriptors = new ArrayList<BluetoothGattDescriptor>();
    private ArrayList<BluetoothGattCharacteristic> listCharacteristics = new ArrayList<>();
    private DispoThunderBoard mDispoThunderBoard;

    public interface OnCounterReadListener {
        void onReadValues(DispoThunderBoard dispo);

        void onConnected(boolean success);
    }

    private Context mContext;
    private OnCounterReadListener mListener;
    private String mDeviceAddress;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothGatt mBluetoothGatt;

    public GattClient(DispoThunderBoard dispo){
        mDispoThunderBoard = dispo;
    }

    private BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "Connected to GATT client. Attempting to start service discovery");
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "Disconnected from GATT client");
                mListener.onConnected(false);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                boolean connected = false;

                /*for(BluetoothGattService gattService : gatt.getServices()){
                    for (BluetoothGattCharacteristic characteristic : gattService.getCharacteristics() ) {
                        gatt.setCharacteristicNotification(characteristic,true);
                        //for(BluetoothGattDescriptor descriptor : characteristic.getDescriptors()){
                        {
                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(ThunderBoardUuids.UUID_DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION);
                            if (descriptor != null) {
                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                connected = gatt.writeDescriptor(descriptor);
                            }
                        }
                        //}
                    }
                }*/

                //BluetoothGattService service = gatt.getService(ThunderBoardUuids.UUID_SERVICE_ACCELERATION_ORIENTATION);

                for(BluetoothGattService gattService : gatt.getServices()){
                    Log.e("SERVICE", Utils.getServiceName(gattService.getUuid()));
                    for (BluetoothGattCharacteristic characteristic : gattService.getCharacteristics() ) {
                        if (characteristic != null) {
                            gatt.setCharacteristicNotification(characteristic, true);

                            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(ThunderBoardUuids.UUID_DESCRIPTOR_CLIENT_CHARACTERISTIC_CONFIGURATION);
                            if (descriptor != null) {
                                descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                listDescriptors.add(descriptor);
                                //este codigo se hara cuando se llame onDescriptorWrite
                                //connected = gatt.writeDescriptor(descriptor);
                            }
                        }
                    }
                }

                //desencadenamos el envio de descriptores
                gatt.writeDescriptor(listDescriptors.get(nextDescriptor++));

                //Llenaos la lista con las caracteristicas que no tienen notificacion
                listCharacteristics.add(gatt.getService(ThunderBoardUuids.UUID_SERVICE_ENVIRONMENT_SENSING).getCharacteristic(ThunderBoardUuids.UUID_CHARACTERISTIC_HUMIDITY));
                listCharacteristics.add(gatt.getService(ThunderBoardUuids.UUID_SERVICE_ENVIRONMENT_SENSING).getCharacteristic(ThunderBoardUuids.UUID_CHARACTERISTIC_TEMPERATURE));
                listCharacteristics.add(gatt.getService(ThunderBoardUuids.UUID_SERVICE_ENVIRONMENT_SENSING).getCharacteristic(ThunderBoardUuids.UUID_CHARACTERISTIC_UV_INDEX));
                listCharacteristics.add(gatt.getService(ThunderBoardUuids.UUID_SERVICE_AMBIENT_LIGHT).getCharacteristic(ThunderBoardUuids.UUID_CHARACTERISTIC_AMBIENT_LIGHT_REACT));

                /*BluetoothGattService service = gatt.getService(ThunderBoardUuids.UUID_SERVICE_ACCELERATION_ORIENTATION);
                if (service != null) {
                    BluetoothGattCharacteristic characteristic = service.getCharacteristic(CHARACTERISTIC_COUNTER_UUID);
                    if (characteristic != null) {
                        gatt.setCharacteristicNotification(characteristic, true);

                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(DESCRIPTOR_CONFIG);
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            connected = gatt.writeDescriptor(descriptor);
                        }
                    }
                }*/
                mListener.onConnected(connected);
            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            readCharacteristic(characteristic);
            if(nextCharacteristic<listCharacteristics.size()){
                gatt.readCharacteristic(listCharacteristics.get(nextCharacteristic++));
            } else {
                nextCharacteristic = 0;
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            readCharacteristicChanged(characteristic);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            /*if (DESCRIPTOR_CONFIG.equals(descriptor.getUuid())) {
                BluetoothGattCharacteristic characteristic = gatt.getService(SERVICE_UUID).getCharacteristic(CHARACTERISTIC_COUNTER_UUID);
                gatt.readCharacteristic(characteristic);
            }*/
            if(nextDescriptor<listDescriptors.size()){
                gatt.writeDescriptor(listDescriptors.get(nextDescriptor++));
                Log.e("onDescriptorWrite", "descriptor" + nextDescriptor + " UUID" + listDescriptors.get(nextDescriptor).getCharacteristic().getUuid().toString());
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);

        }

        private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
            /*if (CHARACTERISTIC_COUNTER_UUID.equals(characteristic.getUuid())) {
                UUID uuid = characteristic.getUuid();
                Log.e("characteristic changed:",Utils.getCharacteristicName(uuid));
                byte[] data = characteristic.getValue();
                int value = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32,0);
                mListener.onCounterRead(value);
            }*/
            UUID uuid = characteristic.getUuid();
            byte[] ba = characteristic.getValue();

            if (ba == null || ba.length == 0) {
                Log.d("Read characteristic","characteristic is not initialized" + uuid.toString());
            } else {
                //ThunderBoardDevice device = bleManager.getDeviceFromCache(gatt.getDevice().getAddress());
                if (ThunderBoardUuids.UUID_CHARACTERISTIC_DEVICE_NAME.equals(uuid)) {
                    String deviceName = characteristic.getStringValue(0);
                    //mDispoThunderBoard.setName(deviceName);
                    //Timber.d("characteristic: %s %s", characteristic.getUuid().toString(), deviceName);
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_MODEL_NUMBER.equals(uuid)) {
                    String modelNumber = characteristic.getStringValue(0);
                    //mDispoThunderBoard.setModelNumber(modelNumber);
                    //Timber.d("modelNumber characteristic: %s %s", characteristic.getUuid().toString(), modelNumber);
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_SYSTEM_ID.equals(uuid)) {
                    /*byte[] bytes = characteristic.getValue();
                    ByteBuffer bb = ByteBuffer.wrap(bytes);
                    bb.order(ByteOrder.BIG_ENDIAN);
                    long id = bb.getLong() & 0xFFFFFF;
                    String systemId = String.valueOf(id);
                    device.setSystemId(systemId);
                    Timber.d("systemId characteristic: %s %s", characteristic.getUuid().toString(),
                            systemId);*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_BATTERY_LEVEL.equals(uuid)) {
                    int batteryLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    //Timber.d("batteryLevel: %d", batteryLevel);
                    mDispoThunderBoard.batteryLevel = batteryLevel;
                    //device.isBatteryConfigured = true;
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_POWER_SOURCE.equals(uuid)) {
                    /*int powerSource = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    Timber.d("powerSource: %d", powerSource);
                    device.setPowerSource(powerSource);
                    device.isPowerSourceConfigured = true;
                    bleManager.selectedDeviceStatusMonitor.onNext(new StatusEvent(device));
                    bleManager.selectedDeviceMonitor.onNext(device);*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_FIRMWARE_REVISION.equals(uuid)) {
                    /*String firmwareVerion = characteristic.getStringValue(0);
                    device.setFirmwareVersion(firmwareVerion);
                    Timber.d("firmware on next");
                    Timber.d("characteristic: %s %s", characteristic.getUuid().toString(), firmwareVerion);
                    // the last from the required read characteristics
                    bleManager.selectedDeviceStatusMonitor.onNext(new StatusEvent(device));
                    bleManager.selectedDeviceMonitor.onNext(device);*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_TEMPERATURE.equals(uuid)) {
                    int temperature = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0);
                    mDispoThunderBoard.Temperature = temperature/100;
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_HUMIDITY.equals(uuid)) {
                    int humidity = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                    mDispoThunderBoard.Humidity = humidity/100;
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_UV_INDEX.equals(uuid)) {
                    int uvIndex = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    mDispoThunderBoard.UV_Index = uvIndex;
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_AMBIENT_LIGHT_REACT.equals(uuid) ||
                        ThunderBoardUuids.UUID_CHARACTERISTIC_AMBIENT_LIGHT_SENSE.equals(uuid)) {
                    int ambientLight = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                    long ambientLightLong = (ambientLight < 0) ?
                            (long) Math.abs(ambientLight) + (long) Integer.MAX_VALUE : ambientLight;
                    mDispoThunderBoard.AmbientLight = ambientLightLong/100;
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_SOUND_LEVEL.equals(uuid)) {
                    /*int soundLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0);
                    Timber.d("sound level: %d", soundLevel);
                    ThunderBoardSensorEnvironment sensor = device.getSensorEnvironment();
                    sensor.setSoundLevel(soundLevel);
                    bleManager.environmentReadMonitor.onNext(new EnvironmentEvent(device, uuid));*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_PRESSURE.equals(uuid)) {
                    /*long pressure = (long) characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32, 0);
                    Timber.d("pressure: %d", pressure);
                    ThunderBoardSensorEnvironment sensor = device.getSensorEnvironment();
                    sensor.setPressure(pressure);
                    bleManager.environmentReadMonitor.onNext(new EnvironmentEvent(device, uuid));*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_CO2_READING.equals(uuid)) {
                    /*int co2Level = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                    Timber.d("C02 level: %d ppm", co2Level);
                    ThunderBoardSensorEnvironment sensor = device.getSensorEnvironment();
                    sensor.setCO2Level(co2Level);
                    bleManager.environmentReadMonitor.onNext(new EnvironmentEvent(device, uuid));*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_TVOC_READING.equals(uuid)) {
                    /*int tvocLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16, 0);
                    Timber.d("TVOC level: %d ppb", tvocLevel);
                    ThunderBoardSensorEnvironment sensor = device.getSensorEnvironment();
                    sensor.setTVOCLevel(tvocLevel);
                    bleManager.environmentReadMonitor.onNext(new EnvironmentEvent(device, uuid));*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_HALL_FIELD_STRENGTH.equals(uuid)) {
                    /*long hallStrength = (long) characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT32, 0);
                    Timber.d("hall strength: %d uT", hallStrength);
                    ThunderBoardSensorEnvironment sensor = device.getSensorEnvironment();
                    sensor.setHallStrength(hallStrength);
                    bleManager.environmentReadMonitor.onNext(new EnvironmentEvent(device, uuid));*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_HALL_STATE.equals(uuid)) {
                    /*@HallState int hallState = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    Timber.d("hall state: %d", hallState);
                    ThunderBoardSensorEnvironment sensor = device.getSensorEnvironment();
                    sensor.setHallState(hallState);
                    bleManager.environmentReadMonitor.onNext(new EnvironmentEvent(device, uuid));*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_CSC_FEATURE.equals(uuid)) {
                    /*byte cscFeature = ba[0];
                    Timber.d("csc feature: %02x %02x", ba[0], ba[1]);
                    ThunderBoardSensorMotion sensor = device.getSensorMotion();
                    sensor.setCscFeature(cscFeature);
                    bleManager.notificationsMonitor.onNext(new NotificationEvent(device,
                            uuid,
                            NotificationEvent.ACTION_NOTIFICATIONS_SET));*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_DIGITAL.equals(characteristic.getUuid())) {
                    //AQUI VA LO DE LOS SWITCH Y LOS LEDS????????????????????????????
                    /*ThunderBoardSensorIo sensor = device.getSensorIo();
                    if (sensor == null) {
                        sensor = new ThunderBoardSensorIo();
                        device.setSensorIo(sensor);
                    }
                    sensor.setLed(ba[0]);
                    sensor.isSensorDataChanged = true;
                    bleManager.selectedDeviceMonitor.onNext(device);
                    return;*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_RGB_LEDS.equals(uuid)) {
                    /*Integer on = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    Integer red = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 1);
                    Integer green = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 2);
                    Integer blue = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 3);

                    LedRGBState ledState = new LedRGBState(
                            on != null && (on != 0),
                            new LedRGB(
                                    red == null ? 0 : red,
                                    green == null ? 0 : green,
                                    blue == null ? 0 : blue
                            )
                    );
//                    Timber.d(String.format("READING Color LED Value: %s", ledState));
                    ThunderBoardSensorIo sensor = device.getSensorIo();
                    if (sensor == null) {
                        sensor = new ThunderBoardSensorIo();
                        device.setSensorIo(sensor);
                    }

                    sensor.setColorLed(ledState);
                    bleManager.selectedDeviceMonitor.onNext(device);*/
                } else {
                    Log.d("Read characteristic", "unknown characteristic " + uuid);
                }

                //Al Final, actualizamos la UI
                mListener.onReadValues(mDispoThunderBoard);
            }
        }

        private void readCharacteristicChanged(BluetoothGattCharacteristic characteristic){
            UUID uuid = characteristic.getUuid();
            // leave for debugging purposes
            // Timber.d("characteristic: %s", uuid.toString());

            byte[] ba = characteristic.getValue();

            if (ba == null || ba.length == 0) {
                Log.d("CharacteristicChanged","characteristic: is not initialized" + uuid.toString());
            } else {
                //ThunderBoardDevice device = bleManager.getDeviceFromCache(gatt.getDevice().getAddress());

                if (ThunderBoardUuids.UUID_CHARACTERISTIC_BATTERY_LEVEL.equals(uuid)) {
                    int batteryLevel = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    mDispoThunderBoard.batteryLevel = batteryLevel;
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_POWER_SOURCE.equals(uuid)) {
                    /*int powerSource = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    Timber.d("Power source: %d", powerSource);
                    device.setPowerSource(powerSource);
                    device.isPowerSourceConfigured = true;
                    bleManager.selectedDeviceStatusMonitor.onNext(new StatusEvent(device));*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_DIGITAL.equals(characteristic.getUuid())) {
                    //LEDS?????????????????????????????????????
                    Log.d("Switches","value: " + ba[0]);
                    switch(ba[0]){
                        case 0b00000000:
                            mDispoThunderBoard.sw0 = 0;
                            mDispoThunderBoard.sw1 = 0;
                            break;
                        case 0b00000001:
                            mDispoThunderBoard.sw0 = 1;
                            mDispoThunderBoard.sw1 = 0;
                            break;
                        case 0b00000100:
                            mDispoThunderBoard.sw0 = 0;
                            mDispoThunderBoard.sw1 = 1;
                            break;
                        case 0b00000101:
                            mDispoThunderBoard.sw0 = 1;
                            mDispoThunderBoard.sw1 = 1;
                            break;
                    }
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_CSC_MEASUREMENT.equals(uuid)) {
                    /*byte wheelRevolutionDataPresent = ba[0];
                    int cumulativeWheelRevolutions = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT32,
                            1);
                    int lastWheelRevolutionTime = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT16,
                            5);
                    Timber.d("csc measurement: %d", cumulativeWheelRevolutions);
                    ThunderBoardSensorMotion sensor = device.getSensorMotion();
                    sensor.setCscMesurements(wheelRevolutionDataPresent,
                            cumulativeWheelRevolutions,
                            lastWheelRevolutionTime);
                    bleManager.motionDetector.onNext(new MotionEvent(device, uuid,
                            MotionEvent.ACTION_CSC_CHANGED));*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_ACCELERATION.equals(uuid)) {
                    int accelerationX = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0);
                    int accelerationY = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 2);
                    int accelerationZ = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 4);
                    mDispoThunderBoard.Acelereation_x = (float)accelerationX/1000;
                    mDispoThunderBoard.Acelereation_y = (float)accelerationY/1000;
                    mDispoThunderBoard.Acelereation_z = (float)accelerationZ/1000;
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_ORIENTATION.equals(uuid)) {
                    int orientationX = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 0);
                    int orientationY = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 2);
                    int orientationZ = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_SINT16, 4);
                    mDispoThunderBoard.Orientation_x = orientationX/100;
                    mDispoThunderBoard.Orientation_y = orientationY/100;
                    mDispoThunderBoard.Orientation_z = orientationZ/100;
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_CALIBRATE.equals(uuid)) {
                    /*Timber.d("onCharacteristicChanged startCalibration value: %02x, length: %d", ba[0], ba
                            .length);
                    if (ba[0] == 0x01) {
                        bleManager.motionDetector.onNext(new MotionEvent(device, uuid, MotionEvent
                                .ACTION_CALIBRATE));
                    } else if (ba[0] == 0x02) {
                        Timber.d("startCalibration completed with orientation reset");
                        bleManager.motionDetector.onNext(new MotionEvent(device, uuid, MotionEvent
                                .ACTION_CLEAR_ORIENTATION));
                    }*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_CSC_CONTROL_POINT.equals(uuid)) {
                    /*Timber.d("onCharacteristicChanged clear rotation value: %02x, length: %d",
                            ba[0], ba.length);
                    Timber.d("clearRotation completed with orientation reset");
                    bleManager.motionDetector.onNext(new MotionEvent(device, uuid, MotionEvent
                            .ACTION_CLEAR_ROTATION));*/
                } else if (ThunderBoardUuids.UUID_CHARACTERISTIC_HALL_STATE.equals(uuid)) {
                    /*@HallState int hallState = characteristic.getIntValue(BluetoothGattCharacteristic.FORMAT_UINT8, 0);
                    Timber.d("onCharacteristicChanged hall state: %d", hallState);
                    ThunderBoardSensorEnvironment sensor = device.getSensorEnvironment();
                    sensor.setHallState(hallState);
                    bleManager.environmentDetector.onNext(new EnvironmentEvent(device, uuid));*/
                }

                mListener.onReadValues(mDispoThunderBoard);
            }
        }
    };

    private final BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    startClient();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    stopClient();
                    break;
                default:
                    // Do nothing
                    break;
            }
        }
    };

    public void onCreate(Context context, String deviceAddress, OnCounterReadListener listener) throws RuntimeException {
        mContext = context;
        mListener = listener;
        mDeviceAddress = deviceAddress;

        mBluetoothManager = (BluetoothManager) context.getSystemService(BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (!checkBluetoothSupport(mBluetoothAdapter)) {
            throw new RuntimeException("GATT client requires Bluetooth support");
        }

        // Register for system Bluetooth events
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mContext.registerReceiver(mBluetoothReceiver, filter);
        if (!mBluetoothAdapter.isEnabled()) {
            Log.w(TAG, "Bluetooth is currently disabled... enabling");
            mBluetoothAdapter.enable();
        } else {
            Log.i(TAG, "Bluetooth enabled... starting client");
            startClient();
        }
    }

    /***
     * Metodo para solocitar informacion de las caracteristicas que no envian notificacion
     */
    public void requestDataThunderBoard(){
        if(mBluetoothGatt != null && nextDescriptor == listDescriptors.size() && listDescriptors.size() > 0 ){  //Si Gatt no es null y ya se madaron todos los descriptores
            mBluetoothGatt.readCharacteristic(listCharacteristics.get(nextCharacteristic++));
        }
    }

    /***
     * Metodo para mandar un valor a la characteristica UUID_SERVICE_AUTOMATION_IO
     * @param commando paramentro con byte (en int) que contiene la informacion de os leds a encender
     */
    public void sendDataLeds(int commando){
        if(mBluetoothGatt != null){
            BluetoothGattService service = mBluetoothGatt.getService(ThunderBoardUuids.UUID_SERVICE_AUTOMATION_IO);
            int property = BluetoothGattCharacteristic.PROPERTY_WRITE;
            List<BluetoothGattCharacteristic> results = new ArrayList<>();
            for (BluetoothGattCharacteristic c : service.getCharacteristics()) {
                int props = c.getProperties();
                if (ThunderBoardUuids.UUID_CHARACTERISTIC_DIGITAL.equals(c.getUuid())
                        && (property == (property & props))) {
                    results.add(c);
                }
            }
            //BluetoothGattCharacteristic characteristic = service.getCharacteristic(ThunderBoardUuids.UUID_CHARACTERISTIC_DIGITAL);
            BluetoothGattCharacteristic characteristic = results.get(0);
            characteristic.setValue(commando, BluetoothGattCharacteristic.FORMAT_UINT8, 0);
            characteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);

            while(nextCharacteristic != 0){
                //Este while es para esperar a que se acaben de leer las caracteristicas sin descriptores
                if(nextCharacteristic >= listCharacteristics.size()) break;
            }

            boolean resp = mBluetoothGatt.writeCharacteristic(characteristic);
            if(resp){
                //Toast.makeText(mContext,"OK", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onDestroy() {
        mListener = null;

        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter.isEnabled()) {
            stopClient();
        }

        mContext.unregisterReceiver(mBluetoothReceiver);
    }

    public void writeInteractor() {
        /*BluetoothGattCharacteristic interactor = mBluetoothGatt
                .getService(SERVICE_UUID)
                .getCharacteristic(CHARACTERISTIC_INTERACTOR_UUID);
        interactor.setValue("!");
        mBluetoothGatt.writeCharacteristic(interactor);*/
    }

    private boolean checkBluetoothSupport(BluetoothAdapter bluetoothAdapter) {
        if (bluetoothAdapter == null) {
            Log.w(TAG, "Bluetooth is not supported");
            return false;
        }

        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.w(TAG, "Bluetooth LE is not supported");
            return false;
        }

        return true;
    }

    private void startClient() {
        BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(mDeviceAddress);
        mBluetoothGatt = bluetoothDevice.connectGatt(mContext, false, mGattCallback);

        if (mBluetoothGatt == null) {
            Log.w(TAG, "Unable to create GATT client");
            return;
        }
    }

    private void stopClient() {
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }

        if (mBluetoothAdapter != null) {
            mBluetoothAdapter = null;
        }
    }
}

