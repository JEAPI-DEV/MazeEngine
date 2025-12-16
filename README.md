# Maze Runner CLI

This is Maze Runner CLI. It runs maze games with bots in Java. Players navigate grids and collect forms to finish.

Built with Java 21 and Maven. Works on any OS that runs Java.

## Setup

Install Java 21 and Maven. Clone the repo. Run `mvn clean package` to build the JAR.

## Usage

Run with: `java -jar MazeRunner-0_5.jar --map <maze.json> --players <num> <player1.jar> ...`

Options:
- --max-turns: turns per player (default 150)
- --randomSpawn: randomize starts (0 or 1)
- --level: 1-5 for features
- --log: stderr logging (1)
- --turnInfo: show turns (1)
- --debug: extra output (0)
- --gui: open Swing viewer

Example: `java -jar MazeRunner-0_5.jar --map Mazes/01_Geradeaus.json --players 2 Players/player1.jar Players/player2.jar --gui`

Mazes are JSON files in the Mazes folder. Players are JARs with stdin/stdout protocol.