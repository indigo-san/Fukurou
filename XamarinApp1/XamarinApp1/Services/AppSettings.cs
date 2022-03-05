using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Threading.Tasks;

namespace XamarinApp1.Services;

public class AppSettings
{
    public static readonly Task<AppSettings> Instance;

    static AppSettings()
    {
        Instance = Task.Run(async () =>
        {
            var file = Path.Combine(StorageHelper.GetPath(), "settings.json");

            if (!File.Exists(file))
            {
                return new AppSettings();
            }
            else
            {
                await using var stream = File.OpenRead(file);
                AppSettings result;
                try
                {
                    result = await JsonSerializer.DeserializeAsync<AppSettings>(stream, StorageHelper.Options);
                }
                catch (Exception ex)
                {
                    result = default;
                }

                if (result == null)
                {
                    File.Delete(file);
                    return new AppSettings();
                }
                else
                {
                    return result;
                }
            }
        });
    }

    public void Save()
    {
    }
}
