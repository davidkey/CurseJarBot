# CurseJarBot
CurseJarBot - A discord bot that monitors and tallies cursing.

### Overview
CurseJarBot uses springboot to start up a simple discord bot that monitors cursing and will warn users when they curse.

### Example:

```
dk "DUCK KING" - Today at 6:35 PM
  shi*!

curse-jar-botBOT - Today at 6:35 PM
  @dk "DUCK KING" - one of us needs to calm down! You've now cursed 51 times for a curse jar balance of $5.10.
```

Users can also check their current curse balance with ```!balances```:

```
dk "DUCK KING" - Today at 6:35 PM
  !balances

curse-jar-botBOT - Today at 6:35 PM  
  Current Curse Jar Balances: 
  dk - $5.10
  Suggestive Night Apparel - $0.30
```

### Build & Run
To build this app, you need to have java 8, maven (3.x) and an internet connection:

```sh
git clone https://github.com/davidkey/CurseJarBot.git
mvn package
```

To run:
```sh
java -jar ./target/cursejarbot-0.0.1-SNAPSHOT.jar
```

Note that you must supply a ```discord.api.token``` - copy ```application.properties.sample``` to ```application.properties``` and update accordingly.

See https://github.com/BtoBastian/Javacord for more information about acquiring a discord api token.
