# Maze Engine Protocol Documentation

## Overview
This document describes the complete protocol and specifications for the VitMaze game engine, a multiplayer maze-based game where players navigate through mazes, collect forms, manage sheets, and compete to reach their finish positions.

## Game Architecture

### Core Components
- **Game Manager**: [`com.codingame.gameengine.core.GameManager`](com/codingame/gameengine/core/GameManager.java:29) - Main game controller
- **Referee**: [`com.codingame.game.Referee`](com/codingame/game/Referee.java:19) - Game logic coordinator
- **Player**: [`com.codingame.game.Player`](com/codingame/game/Player.java:20) - Player entity with actions and state
- **Maze**: [`de.vitbund.vitmaze.game.Maze`](de/vitbund/vitmaze/game/Maze.java:22) - Maze structure and layout
- **Cell**: [`de.vitbund.vitmaze.game.Cell`](de/vitbund/vitmaze/game/Cell.java:21) - Base cell class for maze tiles

## Protocol Specifications

### Timing Constraints
- **Turn Timeout**: 50ms per turn (configurable via [`GameManager.turnMaxTime`](com/codingame/gameengine/core/GameManager.java:45))
- **Initial Timeout**: 1000ms for first turn (configurable via [`GameManager.firstTurnMaxTime`](com/codingame/gameengine/core/GameManager.java:46))
- **Maximum Turns**: 150 turns (configurable via [`Game.maxTurns`](de/vitbund/vitmaze/game/Game.java:20))
- **Debug Mode**: 25 seconds per turn when enabled

### Game Configuration
- **League Level**: 1-5 (progressive difficulty)
- **Fog of War**: Optional visibility restriction
- **Sheets per Player**: 2 sheets (configurable)
- **Random Agent Positions**: Optional randomized starting positions
- **Maze ID**: String identifier for maze selection

## Complete Input/Output Protocol

### Initialization Phase
Each player receives 2 input lines on game start:

**Line 1**: Game configuration
```
[MAZE_WIDTH] [MAZE_HEIGHT] [LEAGUE_LEVEL]
```

**Line 2**: Player initialization (Level 5+ includes sheet count)
```
[PLAYER_ID] [START_X] [START_Y] [SHEETS_PER_PLAYER]
```

### Turn-Based Protocol
Each turn, players receive 6 input lines:

**Line 1**: Last action result
```
[RESULT_STATUS] [RESULT_DETAILS]
```
Examples: `OK NORTH`, `NOK BLOCKED`, `OK 5 3`, `OK FORM`

**Line 2**: Current cell status
```
[CELL_TYPE] [CELL_DETAILS] [OPPONENT_DISTANCE_INFO]
```
Examples: `FLOOR`, `WALL`, `FINISH 1 3`, `FORM 2 1`, `SHEET`, `FLOOR !2`

**Lines 3-6**: Neighboring cell status (NORTH, EAST, SOUTH, WEST)
```
[CELL_TYPE] [CELL_DETAILS] [OPPONENT_DISTANCE_INFO]
```

### Player Output
Players must respond with exactly 1 line containing:
```
[ACTION] [PARAMETER]
```
Examples: `GO NORTH`, `TAKE`, `KICK EAST`, `PUT`, `FINISH`

## Available Commands

### Movement Commands
- **GO [DIRECTION]**: Move player in specified direction
  - Directions: [`NORTH`, `EAST`, `SOUTH`, `WEST`](de/vitbund/vitmaze/game/Direction.java:6)
  - Returns: `OK [DIRECTION]` or `NOK BLOCKED`

### Information Commands
- **POSITION**: Get current player coordinates
  - Returns: `OK [X] [Y]`

### Collection Commands
- **TAKE**: Collect items (forms or sheets)
  - Forms: Collect in alphabetical order (A, B, C, ...)
  - Sheets: Pick up sheets from floor (Level 5+)
  - Returns: `OK FORM`, `OK SHEET`, or `NOK [REASON]`

### Action Commands
- **KICK [DIRECTION]**: Kick objects to adjacent cells
  - Forms: Kick forms to neighboring cells (Level 4+)
  - Sheets: Kick sheets to neighboring cells (Level 5+)
  - Returns: `OK [DIRECTION]` or `NOK [REASON]`

- **PUT**: Place sheet on current cell
  - Only available in Level 5+
  - Returns: `OK` or `NOK [REASON]`

### Game Commands
- **FINISH**: Attempt to finish the game
  - Must have all required forms collected
  - Must be on player's finish cell
  - Returns: `OK` or `NOK [REASON]`

## Action Result Reasons

The following reasons can be returned for failed actions:
- **SHEET**: Sheet-related operation
- **FORM**: Form-related operation  
- **BLOCKED**: Movement or action blocked
- **EMPTY**: No item to interact with
- **NOTYOURS**: Item belongs to another player
- **WRONGORDER**: Forms must be collected in order
- **TALKING**: Player is in talking state
- **TAKING**: Player is in taking state
- **NOTSUPPORTED**: Action not available at current level

## Game Levels and Features

### Level 1: Basic Movement
- Movement commands (GO)
- Position tracking
- Basic maze navigation
- Toroidal maze wrapping (edges connect)

### Level 2: Form Collection
- Form objects appear in maze
- TAKE command to collect forms
- Forms must be collected in alphabetical order (A, B, C, ...)
- Each player has specific forms assigned

### Level 3: Advanced Features
- Enhanced opponent detection
- Distance tracking in cell status
- "Talking" state when players occupy same cell
- Opponent proximity indicators (!1, !2, etc.)

### Level 4: Kicking Forms
- KICK command enabled for forms
- Strategic form positioning
- Form movement to adjacent cells

### Level 5: Sheet Management
- Sheet objects introduced as portable items
- PUT and TAKE commands for sheets
- KICK command extended to sheets
- Sheets serve as movable obstacles/strategic elements

## Sheet Mechanics and Purpose

### What Sheets Are
Sheets are portable objects that players can carry, place, and kick around the maze. They serve as strategic elements for blocking paths, creating barriers, or facilitating movement.

### Sheet Functionality
- **Initial Inventory**: Each player starts with 2 sheets (configurable via [`Game.sheetsPerPlayer`](de/vitbund/vitmaze/game/Game.java:18))
- **Carrying Capacity**: Players can carry multiple sheets in their inventory ([`Player.sheets`](com/codingame/game/Player.java:32))
- **Visual Representation**: Sheets appear as black rectangles on cells ([`SheetRenderer`](de/vitbund/vitmaze/game/renderer/SheetRenderer.java:26))

### Sheet Actions
1. **TAKE**: Pick up a sheet from the current cell
   - Only works if cell has a sheet
   - Adds sheet to player's inventory
   - Triggers "taking" animation state

2. **PUT**: Place a sheet from inventory onto current cell
   - Removes sheet from player's inventory
   - Places sheet on current floor cell
   - Cannot place if cell already has a sheet

3. **KICK**: Move a sheet to an adjacent cell
   - Can kick sheets from current cell to neighboring cell
   - Strategic use for blocking paths or clearing routes
   - Returns sheet to original cell if kick fails

### Strategic Uses of Sheets
- **Path Blocking**: Place sheets to block opponent movement
- **Bridge Creation**: Use sheets to create temporary pathways
- **Resource Management**: Balance carrying sheets vs. mobility
- **Defensive Play**: Kick sheets to block pursuing opponents
- **Offensive Play**: Clear paths by kicking sheets out of the way

### Sheet Interaction Rules
- Only one sheet per floor cell
- Sheets cannot be placed on wall cells or finish cells
- Players can carry unlimited sheets (stack-based inventory)
- Sheet operations are atomic (no partial moves)

## Maze Structure

### Cell Types
- **WallCell**: Impassable barriers ([`de.vitbund.vitmaze.game.WallCell`](de/vitbund/vitmaze/game/WallCell.java:11))
- **FloorCell**: Walkable surfaces with optional items ([`de.vitbund.vitmaze.game.FloorCell`](de/vitbund/vitmaze/game/FloorCell.java:15))
  - Can contain: Forms, Sheets, or be empty
  - Supports sheet placement and removal
- **FinishCell**: Goal positions for each player ([`de.vitbund.vitmaze.game.FinishCell`](de/vitbund/vitmaze/game/FinishCell.java:15))

### Maze Configuration
- Mazes defined in JSON format in `./Mazes/` directory
- Maze ID specified in game properties
- Supports toroidal wrapping (edges connect)
- Dynamic cell discovery with fog of war

## Player State Management

### Player Properties
- Position (X, Y coordinates)
- Collected forms (Stack-based, alphabetical order)
- Sheets inventory (Stack-based, unlimited capacity)
- Talking state (temporary penalty)
- Taking state (sheet pickup animation)
- Finished status (game completion)

### Sheet-Related Player States
- **Taking State**: Temporary state during sheet pickup (prevents other actions)
- **Sheet Inventory**: Stack of carried sheets ([`Player.getSheets()`](com/codingame/game/Player.java:70))
- **Sheet Display**: HUD shows current sheet count ([`PlayerRenderer.sheetText`](de/vitbund/vitmaze/game/renderer/PlayerRenderer.java:60))

### Scoring System
- Form collection: +10 points per form
- Game completion: +100 points
- Last player standing: +20 points
- Talking penalty: -5 points
- Score cannot go below 0
- **Note**: Sheet operations do not directly affect score but enable strategic play

## Collision and Multiplayer Features

### Player Interaction
- Multiple players can occupy same cell
- Form collection is exclusive (first come, first served)
- Sheet management allows sharing and competition
- Players can kick sheets to block opponents
- Sheet placement can create temporary barriers
- Finish conditions are player-specific

### Opponent Detection
- Level 3+: Distance tracking to opponents
- Directional opponent detection
- Cell status includes opponent proximity information
- "Talking" state when players meet (Level 3+)

## Tournament and Challenge System

### Battle Structure
- **Battle**: [`de.vitbund.vitmaze.challenge.Battle`](de/vitbund/vitmaze/challenge/Battle.java:22) - Individual matches
- **Tournament**: [`de.vitbund.vitmaze.challenge.Tournament`](de/vitbund/vitmaze/challenge/Tournament.java:17) - Multi-stage competition
- **Bot**: [`de.vitbund.vitmaze.challenge.Bot`](de/vitbund/vitmaze/challenge/Bot.java:10) - AI participant
- **Participant**: [`de.vitbund.vitmaze.challenge.Participant`](de/vitbund/vitmaze/challenge/Participant.java) - Tournament competitor

### Tournament Stages
1. **Start Battles**: 6 battles with random bot assignment
2. **Medium Battles**: 3 battles with promoted participants
3. **Rough Battles**: 3 battles with further promotion
4. **Final Battles**: 1 battle determining champion

## Communication Protocol

### Input Format
Players receive input lines containing:
1. Game state information
2. Current cell status
3. Available actions
4. Opponent positions (if applicable)
5. Neighboring cell information

### Output Format
Players must respond with:
- Single command per turn
- Command syntax: `ACTION [PARAMETER]`
- Response within timeout limits

## Error Handling

### Timeout Management
- Players exceeding turn timeout are marked as timed out
- Game continues with remaining active players
- Timeout status tracked in [`AbstractPlayer`](com/codingame/gameengine/core/GameManager.java:136)

### Invalid Commands
- Unsupported actions return `NOK NOTSUPPORTED`
- Invalid parameters return `NOK` with appropriate reason
- Malformed commands may result in player deactivation

### Game End Conditions
- Player reaches finish with all forms
- Maximum turns exceeded
- Only one player remaining active
- Tournament ranking determined by score, forms collected, and activity

## Development Notes

### Debug Features
- Debug mode available via [`AgentInfo.debug`](de/vitbund/vitmaze/game/AgentInfo.java:12)
- Remote debugging support with JDWP
- Visual debugging through game engine modules
- Extended timeout in debug mode (25 seconds)

### Extensibility
- Modular design supports additional game levels
- Custom cell types can be implemented
- New actions can be added to [`ActionName`](de/vitbund/vitmaze/game/ActionName.java:6) enum
- Tournament system supports custom promotion rules

This protocol provides a comprehensive framework for implementing maze-based multiplayer games with progressive difficulty, strategic elements, robust error handling, and tournament competition features.