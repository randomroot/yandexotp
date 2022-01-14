# yandexotp

Just a Kotlin library that validates secrets and generates one-time passwords for **Yandex**
two-factor authentication.

**Inspired by**:

- https://github.com/norblik/KeeYaOtp (original source of algorithms)
- https://github.com/beemdevelopment/Aegis

## Installation

To use it with [Gradle](https://gradle.org) insert the following code in your build.gradle:

```bash
repositories {
    mavenCentral()
}

dependencies {
    implementation("ru.mihailpro.lib:yandexotp:1.0")
}
```

To use it with [Maven](https://maven.apache.org/) insert the following code in your pom.xml file:

```xml

<dependency>
  <groupId>ru.mihailpro.lib</groupId>
  <artifactId>yandexotp</artifactId>
  <version>1.0</version>
</dependency>
```

## Usage

### Generate OTP

1) The process of generating a one-time code is quite simple:

```kotlin
import ru.mihailpro.lib.yandexotp.YandexOTPGenerator

// Get a secret from QR code or provided directly by passport.yandex
val secretHash = "LA2V6KMCGYMWWVEW64RNP3JA3IAAAAAAHTSG4HRZPI"
// Get a PIN code, which user decided to use before QR generation
val pin = "7586"
// Call method
val result = YandexOTPGenerator.getCode(secretHash, pin)
// Example output: oactmacq
```

### Validate 2FA secret

2) If you want to make sure in advance that you can process a given secret, there is a special
   method for checking its checksum. This is useful when the user enters the secret manually and
   might accidentally make a mistake when typing

```kotlin
import ru.mihailpro.lib.yandexotp.utils.YandexOTPValidator

// Output: true - if everything is OK, false - if not
val result = YandexOTPValidator.validateSecret(secretHash)
```

### Parse 2FA secret and QR URI

3) You can also use a special class `YandexSecret` to interact with the generator and store the
   secret obtained from the QR code or manual input by using the following method:

```kotlin
import ru.mihailpro.lib.yandexotp.utils.YandexOTPParser

// If you obtained secret string from QR code use:
val resultQR = YandexOTPParser.parseQRSecret(secretHash)

// If you obtained secret string from user manual input
val resultManual = YandexOTPParser.parseManualSecret(secretHash)
```

4) If you don't want to process URI from QR code by yourself please use our method:

**Example URL from QR:**
```text
otpauth://yaotp/test-user?secret=LA2V6KMCGYMWWVEW64RNP3JA3I&name=test.user&track_id=7d60d52842566939afbb08637e160a514f&uid=1544094177&pin_length=16

Contains:
- <yaotp>: means that this string contains data for Yandex.2FA
- <test-user>: unique user name, usually you may see it in email: test-user@ya.ru

Query params:
- secret: used by another yandexotp methods,
- name: user chosen name, can be configured in Yandex.Passport
- pin_length: user-defined PIN length
```
**Method:**
```kotlin
val yandexSecret = YandexOTPParser.parseQRUri(uri)
```

## License

This project is licensed under the GNU General Public License v3.0. See the [LICENSE](LICENSE) file
for details.