using UnityEngine;
using UnityEngine.Audio;
using UnityEngine.UI;

public class VolumeSettings : MonoBehaviour
{
    public AudioMixer AudioMixer;

    public Slider SfxVolumeSlider;
    public Slider MusicVolumeSlider;

    public void SetMusicVolume()
    {
        float volume = MusicVolumeSlider.value;
        AudioMixer.SetFloat("MusicVolume", Mathf.Log10(volume) * 20);
    }

    public void SetSFXVolume()
    {
        float volume = SfxVolumeSlider.value;
        AudioMixer.SetFloat("SFXVolume", Mathf.Log10(volume) * 20);
    }
}
