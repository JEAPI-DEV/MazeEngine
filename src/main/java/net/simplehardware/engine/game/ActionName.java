package net.simplehardware.engine.game;

/**
 * Available game actions as per the protocol
 */
public enum ActionName {
    GO,         // Move in a direction
    POSITION,   // Get current position
    TAKE,       // Collect forms or sheets
    KICK,       // Kick forms or sheets
    PUT,        // Place sheets
    FINISH      // Attempt to finish the game
}
