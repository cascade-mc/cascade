# Cascade

A Minecraft client mod that adds Lua scripting support with an easy-to-use API.

## Features

- **Lua Scripting**: Write custom scripts to extend your Minecraft client
- **Hot Loading**: Load and reload scripts without restarting Minecraft

## Installation

1. Download the latest release
2. Place the `.jar` file in your `.minecraft/mods` folder
3. Launch Minecraft with Fabric

## Usage

### Loading Scripts

Scripts are stored in `.minecraft/cascade/scripts/`. Use the `/loadscript <filename>` command to load them.

### Available APIs

#### Chat API
- `print_chat(message)` - Send a formatted message to chat (supports MiniMessage syntax)

## Building
```bash
./gradlew build
```

The compiled jar will be in `build/libs/

## License

This project is licensed under the MIT License - saee the [LICENSE.txt](LICENSE.txt) file for details.