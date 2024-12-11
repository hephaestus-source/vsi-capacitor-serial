import { Serial } from 'capacitor-serial';

window.testEcho = () => {
    const inputValue = document.getElementById("echoInput").value;
    Serial.echo({ value: inputValue })
}
