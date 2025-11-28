# Game Viewer - Visualization & Replay System

## Overview
The Game Viewer provides a comprehensive visualization and replay system for watching and analyzing maze games. It captures every turn of gameplay with full console output, allowing you to review games like watching a video.

## Features

### 🎥 Live Game Visualization
- **Real-time maze rendering**: See the maze grid with all elements (walls, forms, sheets, finish cells)
- **Player tracking**: Each player is represented by a colored circle with their ID:
  - Player 1: Red
  - Player 2: Blue  
  - Player 3: Green
  - Player 4: Yellow
- **Form and sheet visualization**: See where forms and sheets are located on the grid
- **40x40 pixel cells**: Clear, antialiased rendering for easy viewing

### ⏯️ Timeline Controls
The viewer includes video-player-style controls for replaying games:
- **Play/Pause**: Start or stop automatic playback (1 turn per second)
- **Previous Turn**: Jump to the previous turn
- **Next Turn**: Jump to the next turn
- **Timeline Slider**: Scrub through any turn in the game
- **Turn Counter**: Shows current turn and total turns (e.g., "Turn 5 / 150")

### 📊 Player Status Panel
View detailed information for each player:
- **Position**: Current X, Y coordinates
- **Score**: Current game score
- **Forms**: Number of forms collected
- **Sheets**: Number of sheets held
- **Status**: Active, Finished, or Timed Out

### 📝 Output Viewers
Two tabbed text areas show per-turn information:
- **System.out Tab**: All standard output from the turn
- **System.err Tab**: All error output from the turn

Both tabs automatically scroll to the bottom when updated and display output in a monospace font.

## How It Works

### Automatic Launch
When you click "Run Game" in the main editor:
1. The Game Viewer window opens automatically
2. The game engine starts running in a separate thread
3. After each turn, the viewer updates with the new state
4. You can watch the game progress in real-time

### Snapshot System
The viewer uses a snapshot-based architecture:
- After each turn, the game engine captures:
  - Complete maze state
  - All player positions and stats
  - System.out output from that turn
  - System.err output from that turn
- Snapshots are stored in memory for instant replay
- You can scrub through snapshots without re-running the game

### Output Capture
The system temporarily redirects System.out and System.err during each turn:
- Player process output is captured
- Game engine messages are captured
- After the turn, streams are restored to console
- Both console and viewer show the output

## Window Layout

```
┌─────────────────────────────────────────────────────────────────┐
│  Game Viewer - [Maze Name]                                      │
├──────────────────────────────────┬──────────────────────────────┤
│                                  │  Player Status               │
│                                  │  ┌────────────────────────┐  │
│                                  │  │ Player 1 (Red)         │  │
│         Maze Visualization       │  │ Position: (5, 10)      │  │
│         (Scrollable 800x700)     │  │ Score: 42              │  │
│                                  │  │ Forms: 3 | Sheets: 1   │  │
│                                  │  │ Status: Active         │  │
│                                  │  └────────────────────────┘  │
│                                  │                              │
│                                  │  [Repeated for all players]  │
│                                  │                              │
│                                  │  ┌──────────────────────┐    │
│                                  │  │ ┌─────────┬────────┐ │    │
│                                  │  │ │Sys.out  │Sys.err │ │    │
│                                  │  │ ├─────────┴────────┤ │    │
│                                  │  │ │                  │ │    │
│                                  │  │ │  Turn output...  │ │    │
│                                  │  │ │                  │ │    │
│                                  │  │ └──────────────────┘ │    │
│                                  │  └──────────────────────┘    │
├──────────────────────────────────┴──────────────────────────────┤
│  ◄ ▐▐ ►  [========|=================]  Turn 5 / 150            │
│  Prev Pause Next                    Timeline Slider             │
└─────────────────────────────────────────────────────────────────┘
```

## Usage Tips

### Debugging Player AI
1. Run your game normally
2. When a player behaves unexpectedly, pause the playback
3. Use Previous/Next buttons to step through turns
4. Check the System.err tab for error messages
5. Verify player position and inventory in the status panel

### Analyzing Game Flow
1. Use the timeline slider to jump to key moments
2. Watch form collection patterns
3. Observe player interactions (kicking, sheet placement)
4. Review final scores in the last turn

### Performance Review
1. Count how many turns each player takes
2. Check for timeout messages in System.err
3. Analyze score progression over time
4. Identify bottlenecks or inefficient paths

## Technical Details

### Components
- **GameViewer.java**: Main window (1400x900) with layout management
- **MazeVisualizerPanel.java**: Custom JPanel with Graphics2D rendering
- **GameSnapshot.java**: Immutable state container with PlayerSnapshot inner class
- **GameEngine.java**: Modified to create snapshots and capture output

### Thread Safety
- Snapshots are created on the game thread
- UI updates use SwingUtilities.invokeLater()
- Timer-based playback runs on the Event Dispatch Thread

### Memory Usage
- Each snapshot stores ~1-2KB per player
- 150 turns with 4 players = ~600-1200KB
- Very efficient for typical game lengths

## Future Enhancements
Possible improvements:
- Export replay to file
- Speed controls (0.5x, 2x, etc.)
- Highlight cells on hover with details
- Path tracing showing player movement history
- Statistics panel with graphs
- Comparison mode for multiple replays

## Troubleshooting

**Viewer doesn't open:**
- Ensure you selected at least one player JAR
- Check console for exceptions

**Missing output:**
- Verify player JAR writes to System.out
- Check that game engine is running (not frozen)

**Playback stutters:**
- Large mazes (>50x50) may render slowly
- Try reducing update frequency in Timer

**Can't see all players:**
- Scroll the maze panel if it's larger than view
- Check Player Status panel - inactive players may have timed out
