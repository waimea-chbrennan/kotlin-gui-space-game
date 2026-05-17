# Plan for Testing the Program

The test plan lays out the actions and data I will use to test the functionality of my program.

Terminology:

- **VALID** data values are those that the program expects
- **BOUNDARY** data values are at the limits of the valid range
- **INVALID** data values are those that the program should reject



## Testing UI Initialisation

Does the game show instructions and the mainwindow with planet, location and inventory info?

### Test Data To Use

Run game.

### Expected Test Result

The lore and instructions window appears followed by the main window when dismissed.

---

## Testing Planet Movement: VALID 

Can the player move left and right from planet to planet?

### Test Data To Use

Click the next and previous planet buttons while on a planet not on the boundary of the planets list.
`Starting on Mireth - click next`
`Starting on Crael - click previous`

### Expected Test Result

The next cycle expected is: Velun -> Mireth -> Crael -> Presso

The inverse of this is also true for clicking the previous button.
The new planet image, name, start location and description should update in the planet panel.


---

## Testing Planet Movement: BOUNDARY

Testing ability to move and from to the first and last planet without error. 

### Test Data To Use

Since the planet objects are stored in a list, it is important to consider the cases where an off by one error would cause undefined behaivour or and index out of bounds.
Thus, we need to test functionality when moving to and from Velun and Presso.

`Starting Mireth - click previous`
`Starting Crael - click next`

### Expected Test Result

Mireth should move to velun and Crael should move right to Presso and their images, title, description and location should show up without error

---   

## Testing Planet Movement: INVALID

The player should not be able to move outside of the list of planets as this would cause a crash.

### Test Data To Use

`Velun - click previous`
`Presso - click next`

### Expected Test Result

This would crash the program with an index out of bounds and so we want to reject this input completely.

---   

## Testing Location Movement: VALID

Testing whether we are able to move in a given direction (up down left right) between locations on planets if there is a corresponding node in that direction.

### Test Data To Use

`Velun, Upper pressurised tunnel - click south`

### Expected Test Result

The game should update the current location to reflect this move in this case becoming the Fault Line Mouth and staying on planet.
Additionally, the movement buttons in the location panel should reflect the possible moves from this new location.

---   

## Testing Location Movement: BOUNDARY

The player should be able to travel to and from location nodes that are on the edges of the map.

### Test Data To Use
Player has cold gear in inv
`Velun, fault line mouth - click left`

placing them in:
`Velun, Surface Equipment Yard - click right`

taking them back to fault line mouth

### Expected Test Result

The game should be able to travel to the Equipment Yard and Back as above without error.

---

## Testing Location Movement: BOUNDARY

Test whether locked locations are not able to be travelled to if player does not have correct item.

### Test Data To Use
Player missing enabled gas torch in their inventory.
`Velun, Drill Chamber - click north`

### Expected Test Result

The game does not let them travel to the Deep Bore Terminal becuase they have not progressed far enough.

---   


## Testing Location Movement: INVALID

Test how the game handles the player trying to move off the map to a node that does not exist.

### Test Data To Use

`Velun, Cracked Ice Shelf - click south`
`Mireth, Communications Tower - click north`

### Expected Test Result

The player should not be able to move off the map at any time as this would cause undefined behaviour. The game should ignore this input setting isDisabled for the buttons.

---

## Testing Item Pickup: VALID

The player should be able to pick up an item if there is one in their current location.

### Test Data To Use

`Mireth, evacuated barracks - click pick up`

### Expected Test Result

The game should allow them to pick up the Field Chip Reader, removing this from the map so they cannot pick it up twice and adding it to their inventory.

---   

## Testing Item Pickup: INVALID

I will test how the player trying to pick up an item is handled when there is no item in location to acquire.

### Test Data To Use

`Mireth, Garrison Perimeter Gate - click pick up`

### Expected Test Result

The game should not allow this action as there is no item to pick up at this location so the input will be ignored with a disabled button.

---   

## Testing Location Unlock: VALID

What happens when the player has an item in their inventory, and they try to go to a location locked with that item?

### Test Data To Use

on Velun, player needs to collect Welding Gear, the welding torch and collect new ignitor before navigating to the Drill Chamber where they then try to move North into the Deep Bore Terminal.

### Expected Test Result

Since Deep Bore terminal relies on the Velun Gas torch and all the dependancies are enabled, the player should be able to successfully move to this new location.

---   

## Testing Item Enable: VALID

What happens when the player picks up another item that satisfies the dependency of an item in their inventory?

### Test Data To Use

Player on Velun travels to pick up Welding Gear, Welding Torch (cold gear also need to reach welding torch).
Test what happens when they pick up the replacement ignitor from Crew Quarters.

### Expected Test Result

The replacement ignitor when picked up in the inventory should satisfy the welding torch and the ignitor is satisfied by the safety gear so all of these items 
should collapse into a single Welding Torch with the description changing to reflect that it has a good ignitor.

---   

## Testing Location Unlock: BOUNDARY / EDGE

What happens when the player has the required item to unlock a location, but it is not enabled?

### Test Data To Use

Navigate to Velun, pick up the Welding Gear, and travel South to pick up Cold Gear, and the Welding Torch.
Then navigate North to Drill Chamber and try to move into Deep Bore Terminal.

### Expected Test Result

Since the inventory is missing the Replacement Ignitor, the Safety Gear is still enabled but the Torch is not because it needs the ignitor. 
Therefore, the player should not be able to move north into Deep Bore and the input should be rejected.

---   


## Testing Win and Lose Conditions

Can the player win by getting the hyprdrive ship and lose by running out of time?

### Test Data To Use

Losing: let the timer run to 0 before escaping. 

Winning: navigate, collect all items and get ship before timer gets to 0 (you aren't getting all the directions lol)

### Expected Test Result

Losing: the mainwindow shall close and the player will recieve a YOU LOSE window that makes them _feel good about themselves_

Winning: similar to losing but the winning window says YOU win congrats for escaping but no confetti cannon, unfortunately.


---   

## Testing JSON validation: INVALID

How does the game cope if the JSON for the planets, items, and locations is modified?

### Test Data To Use

`change Presso startLocationId to waimea_college_e5`
`change presso_maintainance_shaft downId from presso_fuel_storage to presso_drill_arm_bay`
`change crael_transmission_fragment locationId to waimea_college_e5`

### Expected Test Result

All of these data changes should mean the game is unplayable so the program should exit and save the user frustration with a useful debug message.

---   



