import machine
import time
import urequests
import network
import ujson

# Define your Wi-Fi credentials
wifi_ssid = 'Wokwi-GUEST'
wifi_password = ''

# Connect to Wi-Fi
wifi = network.WLAN(network.STA_IF)
wifi.active(True)
wifi.connect(wifi_ssid, wifi_password)

# Wait for Wi-Fi connection
while not wifi.isconnected():
    pass

# Define GPIO pins for ultrasonic sensors
trig_pins = [21, 23]  # Example pins, use the appropriate pins for your setup
echo_pins = [19, 22]  # Example pins, use the appropriate pins for your setup

# Define GPIO pins for servo motors (6 servos)
servo_pins = [13, 14, 25, 26, 27, 33]  # Example pins, use the appropriate pins for your setup
servos = [machine.PWM(machine.Pin(pin), freq=50, duty=0) for pin in servo_pins]

# Firebase Realtime Database URL and secret
firebase_url = 'https://iot-smart-water-manageme-6f2dc-default-rtdb.asia-southeast1.firebasedatabase.app'
firebase_secret = 'PuZIQmVtENsSNLybJ4wDwEHzXUZiiKxsCgh7j6SS'

# Function to measure distance using ultrasonic sensor
def measure_distance(trig_pin, echo_pin):
    trig = machine.Pin(trig_pin, machine.Pin.OUT)
    echo = machine.Pin(echo_pin, machine.Pin.IN)
    
    # Ensure the trigger pin is low
    trig.off()
    time.sleep_us(2)
    
    # Generate a 10us pulse on the trigger pin
    trig.on()
    time.sleep_us(10)
    trig.off()
    
    # Measure the duration of the pulse on the echo pin
    while echo.value() == 0:
        pulse_start = time.ticks_us()
    while echo.value() == 1:
        pulse_end = time.ticks_us()
    
    # Calculate the duration of the pulse
    pulse_duration = time.ticks_diff(pulse_end, pulse_start)
    
    # Calculate the distance based on the speed of sound
    distance = (pulse_duration / 2) / 29.1  # Speed of sound in air is approximately 343 m/s
    
    return distance


# Function to calculate the water level percentage
def calculate_water_level_percentage(current_distance, min_distance, max_distance):
    if current_distance < min_distance:
        return 0
    elif current_distance > max_distance:
        return 100
    else:
        return ((current_distance - min_distance) / (max_distance - min_distance)) * 100
# Define the minimum and maximum distances your sensor can detect
min_distance = 2  # Example minimum distance (in cm)
max_distance = 400  # Example maximum distance (in cm)

# Function to send water level to Firebase
def send_water_level_to_firebase(water_level_percentage, sensor_number):
    data = {'WaterLevel': water_level_percentage}
    url = f'{firebase_url}/sensor_{sensor_number}.json?auth={firebase_secret}'

    try:
        response = urequests.patch(url, json=data)
        if response.status_code == 200:
            print(f"Data for Sensor {sensor_number} sent to Firebase")
        else:
            print(f"Failed to send data to Firebase. Status code: {response.status_code}")
    except Exception as e:
        print(f"Error sending data to Firebase: {str(e)}")

while True:
    # Measure water level using ultrasonic sensors
    for sensor_number in range(len(trig_pins)):
        ultrasonic_distance = measure_distance(trig_pins[sensor_number], echo_pins[sensor_number])
    
        # Calculate and print water level percentage
        water_level_percentage = calculate_water_level_percentage(ultrasonic_distance, min_distance, max_distance)
        print(f"Water Level (Sensor {sensor_number + 1}): {water_level_percentage:.2f}%")
    
        # Send data to Firebase
        send_water_level_to_firebase(water_level_percentage, sensor_number)
    
        # Control the servo valve based on the water level percentage
        if water_level_percentage < 50:
            # Adjust servo control logic based on the water level percentage
            servos[sensor_number].duty(512)  # Set servo duty cycle to a value to control it
        else:
            servos[sensor_number].duty(0)  # Set servo duty cycle to 0 to stop it
    send_water_level_to_firebase(water_level_percentage, sensor_number)
    time.sleep(1)  # Adjust the sleep duration as needed
