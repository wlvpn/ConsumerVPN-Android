# Known VPN SDK issues

## Incompatible Gradle will break obfuscation 
If the client application is using Gradle 3.5 or bellow, the obfuscation process will not obfuscate any variables o class. 

The only known fix is to upgrade Gradle to 3.6+ to use R8 obfuscation. 