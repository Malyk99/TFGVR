using UnityEngine;

public class MusicManager : MonoBehaviour
{
    public AudioSource Background;
    bool MusicPlaying = true;

    public void ChangeMusicStatus()
    {
        if (MusicPlaying)
        {
            Disable();
        }
        else
        {
            Enable();
        }
    }

    private void Enable()
    {
        Background.UnPause();
        MusicPlaying = true;
    }

    private void Disable()
    {
        Background.Pause();
        MusicPlaying = false;
    }
}
