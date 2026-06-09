# ChatFormatter

**Lightweight, LuckPerms-powered chat formatting plugin for Nukkit / Lumi servers.** Automatically applies player prefix, suffix, and group-based formatting to all chat messages with zero configuration.

> **Version:** 1.0.1  
> **Author:** Natro  
> **API:** 1.0.11–1.0.15  
> **Dependency:** LuckPerms

---

## Features

- **Automatic formatting** — Everything works out of the box. Prefix and suffix from LuckPerms are applied to every chat message.
- **Group-based formatting** — Each permission group can have its own format template.
- **Lightweight** — Minimal overhead, no database, no complex configuration.
- **Full compatibility** — Works with Nukkit-MOT and Lumi-based servers.

---

## Installation

1. Install [LuckPerms](https://luckperms.net/) on your server.
2. Place `ChatFormatter.jar` into your `plugins/` folder.
3. Restart the server.

> **Note:** This plugin **requires** LuckPerms and will not function without it.

---

## Permissions

| Permission | Default | Description |
|---|---|---|
| `chatformat.bypass` | `op` | Bypass chat formatting (send raw messages) |
| `chatformatter.reload` | `op` | Reload the plugin configuration |

---

## Commands

| Command | Description |
|---|---|
| `/chatformatter reload` | Reload the plugin configuration |

---

## Building

```bash
mvn clean package
```

---

## License

MIT License — see [LICENSE](LICENSE).
