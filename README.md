# Booklet
A server side, fully data driven guidebook mod! Designed to allow easy and language-dependent guidebooks
for Polymer-powered mods (or is just server side by nature).

![](https://cdn.modrinth.com/data/xxtIpapP/images/6758fc1138cb8f2aa2feb5335c656afee79f7f4e.png)

## Dependencies
This mod depends on Polymer library and server resource packs to work! 
Currently, there is no way to use this without a resource pack.
Bedrock isn't supported either.

## Usage:
Add it to your dependencies like this:

```
repositories {
	maven { url 'https://maven.nucleoid.xyz' }
}

dependencies {
	modImplementation "eu.pb4:booklet:[TAG]"
}
```

See https://github.com/Patbox/booklet/blob/master/USAGE.md for information about creation of guidebooks.