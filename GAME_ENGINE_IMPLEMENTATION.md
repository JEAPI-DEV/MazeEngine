# Game Engine Implementation Summary

## Overview
This document describes the complete game engine implementation for Maze Runner, which follows the protocol specified in `MAZE_RUNNER_PROTOCOL.md`.

## Architecture

### Core Components

#### 1. **GameEngine** (`engine/core/GameEngine.java`)
The main coordinator that manages the entire game flow.

**Key Features:**
- Initializes player processes from JAR files
- Sends protocol-compliant initialization data to players
- Manages turn-by-turn game execution
- Captures stdout/stderr from both server and player processes
- Creates game snapshots for visualization
- Enforces timeouts (50ms per turn, 1000ms first turn)

**Protocol Implementation:**
```
Initialization (sent to each player):
  Line 1: MAZE_WIDTH MAZE_HEIGHT LEAGUE_LEVEL
  Line 2: PLAYER_ID START_X START_Y [SHEETS_PER_PLAYER]

Each Turn (sent to each player):
  Line 1: Last action result
  Line 2: Current cell info
  Lines 3-6: North, East, South, West cell info
```

#### 2. **PlayerProcess** (`engine/core/PlayerProcess.java`)
Wrapper for player JAR subprocess with proper I/O handling.

**Key Features:**
- Launches player JAR using `java -jar`
- **Separate stdout and stderr streams** (no redirectErrorStream)
- Background thread continuously captures stderr
- Buffered storage of stdout and stderr for later retrieval
- Timeout handling using ExecutorService
- Clean process lifecycle management

**Methods:**
- `sendLine(String)` - Send data to player's stdin
- `readLine(long timeout)` - Read player's stdout with timeout
- `getStdout()` - Get all stdout captured since last clear
- `getStderr()` - Get all stderr captured since last clear
- `clearOutput()` - Reset output buffers for next turn

#### 3. **Referee** (`engine/core/Referee.java`)
Enforces game rules and validates player actions.

**Key Features:**
- Processes all action types: GO, TAKE, KICK, PUT, FINISH
- Validates actions against league level restrictions
- Manages player interactions (kicking, stealing forms)
- Calculates scores and determines winner
- Enforces form restrictions per league level

**League Levels:**
- Level 1: GO only
- Level 2: GO, TAKE, KICK
- Level 3: GO, TAKE, KICK, PUT
- Level 4: GO, TAKE, KICK, PUT (with collected forms must match assigned)
- Level 5+: All actions + sheet mechanics

#### 4. **Maze** (`engine/game/Maze.java`)
Represents the game board and provides cell information.

**Key Features:**
- Loads maze from JSON data
- Returns protocol-formatted cell information
- Manages cell types (Wall, Floor, Finish)
- Handles form placement and ownership
- Provides start positions for players

### Game Flow

1. **Initialization**
   ```
   GameEngine.initialize()
   ├─ Send maze dimensions and league level to each player
   ├─ Send player ID, start position, and sheet count
   └─ Log initialization details
   ```

2. **Game Loop**
   ```
   GameEngine.runGame()
   ├─ Create GameViewer window
   ├─ For each turn until game over:
   │  ├─ Clear player output buffers
   │  ├─ Send turn data (6 lines) to each active player
   │  ├─ Read player action with timeout
   │  ├─ Process action through Referee
   │  ├─ Capture player stderr output
   │  └─ Create snapshot for visualization
   └─ Print final results and cleanup
   ```

3. **Visualization**
   ```
   GameViewer
   ├─ Timeline slider for turn navigation
   ├─ Play/Pause controls for automatic playback
   ├─ Maze visualization with player positions
   ├─ Player status panel (score, forms, sheets, position)
   ├─ Output tab (System.out from server)
   └─ Errors tab (System.err from server + player stderr)
   ```

## Cell Types

### WallCell
- Represented as `#` in output
- Not passable

### FloorCell
- Represented as `.` (empty) or letter (form)
- Can contain forms with ownership
- Output format: `FLOOR FORM OWNER` or `FLOOR NONE NONE`

### FinishCell
- Represented as `*`
- Players must reach this to win
- Output format: `FINISH`

## Action Results

### Success Results
- `OK` - Action succeeded
- `STOPPED` - GO action blocked by wall/player
- `STOLE` - Successfully kicked and stole form
- `PUSHED` - Kicked player without stealing

### Failure Results
- `INVALID` - Invalid action format
- `DENIED` - Action not allowed (level restriction, wrong form, etc.)
- `TIMEOUT` - Player exceeded time limit

## Player JAR Protocol

### Expected Bot Behavior

```java
public class ExampleBot {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // First turn: read initialization
        String[] init1 = scanner.nextLine().split(" ");
        int width = Integer.parseInt(init1[0]);
        int height = Integer.parseInt(init1[1]);
        int level = Integer.parseInt(init1[2]);
        
        String[] init2 = scanner.nextLine().split(" ");
        int playerId = Integer.parseInt(init2[0]);
        int startX = Integer.parseInt(init2[1]);
        int startY = Integer.parseInt(init2[2]);
        int sheets = (init2.length > 3) ? Integer.parseInt(init2[3]) : 0;
        
        // Each turn:
        while (true) {
            String result = scanner.nextLine();      // Last action result
            String current = scanner.nextLine();     // Current cell
            String north = scanner.nextLine();       // North cell
            String east = scanner.nextLine();        // East cell
            String south = scanner.nextLine();       // South cell
            String west = scanner.nextLine();        // West cell
            
            // Use System.err for debugging (will appear in Errors tab)
            System.err.println("Turn input received");
            
            // Send action to stdout
            System.out.println("GO NORTH");
        }
    }
}
```

## Integration with UI

### Running a Game

1. User selects player JAR files via **Players** button
2. User clicks **Run Game** button in dialog
3. System creates `GameEngine` with selected JAR paths
4. Engine initializes all player processes
5. `GameViewer` window opens showing live game
6. Timeline allows scrubbing through turns
7. Player stdout/stderr visible in output panel

### Files Involved

- `dialogs/PlayerSelectionDialog.java` - JAR file selection
- `dialogs/RunGameDialog.java` - Game configuration (league, turns, timeout)
- `engine/GameLauncher.java` - Bridge between UI and engine
- `MazeEditor.java` - Main UI with Players and Run Game buttons

## Testing

To test the game engine:

1. Create a simple bot JAR that follows the protocol
2. Use the player selection dialog to load 1-4 player JARs
3. Click "Run Game" in the dialog
4. Observe the game in GameViewer
5. Check output tabs for player stdout/stderr

### Example Test Bot

See the protocol example above. Compile to JAR:

```bash
javac ExampleBot.java
jar cfe bot.jar ExampleBot ExampleBot.class
```

## Configuration

Game parameters in `GameEngine.GameConfig`:

- `leagueLevel` - Game difficulty (1-5+)
- `maxTurns` - Maximum turns before game over
- `turnTimeoutMs` - Timeout per turn (default 50ms)
- `firstTurnTimeoutMs` - Timeout for first turn (default 1000ms)
- `sheetsPerPlayer` - Sheets for level 5+ (default 2)

## Key Improvements

### Compared to Initial Implementation

1. ✅ **Proper stdout/stderr separation**
   - Previous: `redirectErrorStream(true)` merged streams
   - Now: Separate readers for stdout (commands) and stderr (debug)

2. ✅ **Background stderr capture**
   - Previous: Stderr was lost or merged
   - Now: Background thread continuously captures stderr

3. ✅ **Protocol-compliant initialization**
   - Previous: No initialization sent
   - Now: Sends maze size, level, player info at start

4. ✅ **Turn-by-turn output tracking**
   - Previous: No per-turn output
   - Now: Captures and displays output for each turn

5. ✅ **Complete visualization**
   - Previous: No viewer
   - Now: Full timeline with player status and output

## Future Enhancements

- [ ] Add replay save/load functionality
- [ ] Support for spectator mode (watch live without recording)
- [ ] Network multiplayer support
- [ ] Tournament mode (bracket system)
- [ ] Statistics and analytics
- [ ] Custom maze editor integration with game testing
