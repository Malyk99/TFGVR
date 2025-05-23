# 🕹️ Little Demons - Juego Multijugador con Firebase

**Little Demons** es un juego multijugador en tiempo real que conecta una app Android con un videojuego desarrollado en Unity mediante Firebase Realtime Database. Este proyecto ha sido desarrollado como Trabajo de Fin de Grado por ALVYK Games S.L.

---

## 📦 Estructura del Proyecto

### 🔹 Aplicación Android (Java)
- **Activities** para unirse a salas, ver el lobby y participar en minijuegos.
- **Integración con Firebase** usando Realtime Database.
- **Funciones para administradores**: crear/borrar salas, gestionar jugadores y supervisar el estado de la partida.
- **Minijuegos** interactivos sincronizados con Unity.

### 🔹 Juego en Unity (C#) con Realidad Virtual

La experiencia de juego se desarrolla en **Unity**, y está diseñada para ser compatible con **dispositivos de realidad virtual (VR)**. El jugador principal entra en un entorno 3D inmersivo donde puede interactuar con el mundo del juego utilizando mandos de VR y controladores de movimiento.

#### Características VR destacadas:
- **Compatibilidad con visores VR** (como Oculus Quest o similares mediante Unity XR).
- **Interacciones físicas**: El jugador puede disparar flechas, recoger objetos o moverse por escenarios diseñados en 3D.
- **Jugabilidad asimétrica**: Mientras el jugador con visor VR actúa dentro del entorno virtual (por ejemplo, disparando a objetivos o esquivando obstáculos), los jugadores móviles participan a través de acciones que influyen en el mundo del jugador principal (como activar trampas o ayudar con pistas).

El diseño promueve una experiencia cooperativa y sincronizada entre dispositivos móviles y el jugador en VR, ofreciendo un enfoque original de interacción multiplataforma y multijugador.

---

## 🛠️ Tecnologías Utilizadas

- **Firebase Realtime Database** – Sincronización multijugador y almacenamiento de datos
- **Android Studio (Java)** – Aplicación móvil para control de juego e interfaz de usuario
- **Unity (C#)** – Lógica central del juego y experiencia 3D
- **SDKs de Firebase** para Unity y Android
- **RecyclerView** y adaptadores personalizados
- **Diseños XML personalizados + Material UI**

---

## 🚀 Funcionamiento General

1. **Creación de Sala**
   - Los administradores o jugadores crean una sala de juego (pública o privada).
   - Se genera un código de 6 dígitos (`roomCode`) y se guarda en Firebase.

2. **Unirse a la Partida**
   - Los jugadores se unen a la sala mediante el código o seleccionándola de una lista.
   - Acceden al lobby y marcan su estado como "Listo".

3. **Inicio del Juego**
   - Cuando todos están listos, el administrador (o el sistema) inicia el juego.
   - Unity detecta el cambio de estado e inicia el minijuego.

4. **Interacción en el Minijuego**
   - Los jugadores en Android interactúan (botones, giroscopio, etc.).
   - Unity escucha estos cambios y reacciona en tiempo real.

5. **Fin del Juego**
   - Al terminar, todos los jugadores regresan al lobby.

---

## 🔑 Estructura de Firebase (resumen)

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
