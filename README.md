# 🕹️ Little Demons - Firebase Multiplayer Game

**Little Demons** is a real-time multiplayer mobile game that connects Android clients and a Unity-based game using Firebase Realtime Database. This project was developed as a final degree project by ALVYK Games S.L.

---

## 📦 Project Structure

### 🔹 Android App (Java)
- **Activities** for room joining, waiting lobbies, and minigames.
- **Firebase integration** using Realtime Database.
- **Admin features**: Create/delete rooms, manage players, and monitor game state.
- **Minigames**: Interactive mini experiences that sync with the Unity game.

### 🔹 Unity Game (C#)
- Syncs with Firebase to:
  - Listen for game state changes
  - Receive player actions
  - Trigger in-game events
- Players control parts of the gameplay using physical or on-screen controls.
- VR features and visual polish for immersive gameplay.

---

## 🛠️ Technologies Used

- **Firebase Realtime Database** – Multiplayer sync and data storage
- **Android Studio** – Java app for game control and UI
- **Unity (C#)** – Core game logic and 3D interaction
- **Firebase SDKs** for Unity and Android
- **RecyclerView** and custom adapters for dynamic lists
- **Material UI + Custom XML layouts**

---

## 🚀 How It Works

1. **Room Creation**
   - Admins or players create a game room (with optional privacy).
   - A 6-digit `roomCode` is generated and saved in Firebase.

2. **Join Game**
   - Players join rooms via code or room list.
   - They enter a waiting lobby and toggle "Ready" state.

3. **Game Start**
   - Once all players are ready, the admin (or system) starts the game.
   - Unity detects the state change and begins the minigame.

4. **Minigame Interaction**
   - Android clients trigger interactions (buttons, gyroscope, etc.)
   - Unity listens and responds in real time.

5. **Game End**
   - After completion, players are returned to the lobby.

---

## 🔑 Firebase Structure (Simplified)

```plaintext
rooms/
  123456/
    private: false
    players/
      user1/
        ready: true
      user2/
        ready: false
    minigames/
      minigame1/
        blocker1: true
        blocker2: false
      minigame4/
        spawnBomb: true
