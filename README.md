# BujoAssistant
BujoAssistant is an (experimental) Android App for managing tasks in a "bullet journal" way. It also stands as a playground for different architecture patterns and frameworks such as:
* Jetpack Compose
* Redux
* Kodein
* Android Paging

## Task Management
The actual task managment the app performs is minimal. One provides only a description of the task and a scheduled time to do the task. 
The key feature is the *flexible* scheduling of the task, assigning to task to a specific "scope" and not strictly a specific date and time.

Scopes come in different sizes, representing either a Day, Week, Month, or Year. It is not required that every task is assigned a scope,
So a task can be scheduled to be scheduled on a certain day, or the week after next, or January 2023, or in the worst case just *sometime*.

The dashboard of the app focuses on only the tasks that have been scheduled for the current scopes (today, this week, this month, this year). 

The actions one can take on each task are:
1. Complete
2. Uncomplete
3. Defer[^1]
4. Reschedule (not implemented yet)
5. Delete

[^1]: Defering is rescheduling the task, but specifically rescheduling it to the next instance of its scope (i.e. "this week" becomes "next week", "next month" becomes "the month after next", etc.).

## 🌽 Corndux
Corndux is a Redux-like pattern, designed for use in an Android project with Jetpack Compose.

![image](https://user-images.githubusercontent.com/65869260/191126643-d55011f4-c2ad-479e-b4e3-5525ba7bc4fc.png)

**State**: Data class containing all information needed to properly manage and update the display of the UI. Unique to each Store.

**Action**: Any event, triggered by the UI or within the Store, that would produce a side effect or update the state.

**Side Effect**: Any one-time action that needs to be performed, but doesn't need to persist. Examples include performing navigation, firing analytic events, and showing modals/pop-ups.

**Performer**: Entity responsible for "performing" Actions. It can fire off Side Effects or any number of other Actions, performing async work if necessary. A Store can have any number of Performers and they will be called in the order they are provided.

**Reducer**: Entity responsible for "interpreting" Actions. It is responsible for updating the State if necessary for a given Action. A Store can have any number of Reducers and they will be called in the order they were provided.

### Corndux with Jetpack Compose
BujoAssistant utilizes mutliple stores that exist within a hierarchy that corresponds to the Composable hierarchy. 

![image](https://user-images.githubusercontent.com/65869260/191142814-932ef5a2-0614-4111-a223-d8082d404412.png)

Side Effects and Actions are propogated up the hierarchy, but each Store still maintains its own State. 
Actions fired will be handled by the Store that dispatched it before being passed up the tree (Stores lower in the hierarchy will not receive Actions / Side Effects dispatched by Stores above them). 

Side Effects are passed up the hierarchy regardless if the Store has gotten a chance to act on it or not. 

> ℹ It is still being decided if and how a Store could "consume" certain Actions and prevent them from being passed up the hierarchy.

One can use the provided `WithGlobalStore()` method to set the global Store and the `WithStore()` method to provide more context-specific Stores.  

One can fire events to the closest Store by accessing `LocalStore.current` and calling the `dispatch()` method.
