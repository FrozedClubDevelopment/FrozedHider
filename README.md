# FrozedHider

A Minecraft plugin that uses the WorldGuard API to hide players within designated regions. When a player enters a
specified WorldGuard region, they become invisible to other players, and upon exiting, they are revealed again. This is
all done with packets, so players are not removed from the Tablist.

## Features

- Lightweight and easy to use.
- Hide players in configurable WorldGuard regions using the `hide-player` flag.

## Dependencies

- [PaperSpigot 1.21.4](https://fill-data.papermc.io/v1/objects/5ee4f542f628a14c644410b08c94ea42e772ef4d29fe92973636b6813d4eaffc/paper-1.21.4-232.jar)
- [WorldGuard v7.0.13](https://dev.bukkit.org/projects/worldguard/files/6201343/download)

## Building

To build the project, you will need:

- Java 21 or higher
- Maven

Clone the repository and run the following command:

```bash
mvn clean package
```

## Installation

1. Download the latest release from the [releases page](https://github.com/FrozedClub/frozedhider/releases).
2. Place the `frozedhider-1.0.0.jar` file in your server's `plugins` folder.
3. Restart or reload your server.

## Configuration

The configuration is located in `plugins/FrozedHider/config.yml`.

```yaml
# This will show a message on chat when a player enters or exits a region.
debug: false
```

## Author

This plugin is developed and maintained by [Elb1to](https://elb1to.me).

## License

This project is licensed under the FCDL-2.0 License - see the [LICENSE](LICENSE) file for details.

