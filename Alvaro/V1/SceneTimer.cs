using UnityEngine;
using UnityEngine.SceneManagement;

public class SceneTimer : MonoBehaviour
{
    float LoadTime;

    void Awake()
    {
        LoadTime = 1;
    }

    void Update()
    {
        do
        {
            LoadTime -= Time.deltaTime;
        }
        while (LoadTime > 0);

        SceneManager.LoadScene("MainMenu");
    }
}
