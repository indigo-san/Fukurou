using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Text.Json;
using System.Text.Json.Serialization;
using System.Threading.Tasks;

using Xamarin.Forms;

using XamarinApp1.Models;

namespace XamarinApp1.Services;

public class StorageHelper
{
    public static JsonSerializerOptions Options { get; } = new()
    {
        Converters =
        {
            new TimeOnlyJsonConverter(),
            new DateOnlyJsonConverter(),
            new ColorJsonConverter()
        }
    };

    public static string GetPath()
    {
        return Environment.GetFolderPath(Environment.SpecialFolder.LocalApplicationData);
    }

    public static async ValueTask<T> Pull<T>(string name, Func<T> factory)
    {
        var file = Path.Combine(GetPath(), name);

        if (!File.Exists(file))
        {
            return factory();
        }
        else
        {
            await using var stream = File.OpenRead(file);
            T result;
            try
            {
                result = await JsonSerializer.DeserializeAsync<T>(stream, Options);
            }
            catch (Exception ex)
            {
                result = default;
            }

            if (result == null)
            {
                File.Delete(file);
                return factory();
            }
            else
            {
                return result;
            }
        }
    }

    public static async ValueTask<bool> Push<T>(string name, T data)
    {
        try
        {
            var dir = GetPath();
            var file = Path.Combine(dir, name);

            if (!Directory.Exists(dir))
            {
                Directory.CreateDirectory(dir);
            }

            await using var stream = new FileStream(file, FileMode.Create);
            await JsonSerializer.SerializeAsync(stream, data, Options);

            return true;
        }
        catch (Exception ex)
        {
            return false;
        }
    }

    private sealed class TimeOnlyJsonConverter : JsonConverter<TimeOnly>
    {
        public override TimeOnly Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
        {
            var str = reader.GetString();
            if (TimeOnly.TryParse(str, out var timeOnly))
            {
                return timeOnly;
            }
            else
            {
                throw new Exception("Invalid TimeOnly");
            }
        }

        public override void Write(Utf8JsonWriter writer, TimeOnly value, JsonSerializerOptions options)
        {
            writer.WriteStringValue(value.ToString());
        }
    }

    private sealed class DateOnlyJsonConverter : JsonConverter<DateOnly>
    {
        public override DateOnly Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
        {
            var str = reader.GetString();
            if (DateOnly.TryParse(str, out var dateOnly))
            {
                return dateOnly;
            }
            else
            {
                throw new Exception("Invalid DateOnly");
            }
        }

        public override void Write(Utf8JsonWriter writer, DateOnly value, JsonSerializerOptions options)
        {
            writer.WriteStringValue(value.ToString());
        }
    }

    private sealed class ColorJsonConverter : JsonConverter<Color>
    {
        public override Color Read(ref Utf8JsonReader reader, Type typeToConvert, JsonSerializerOptions options)
        {
            var str = reader.GetString();
            return Color.FromHex(str);
        }

        public override void Write(Utf8JsonWriter writer, Color value, JsonSerializerOptions options)
        {
            writer.WriteStringValue(value.ToHex());
        }
    }
}

public class StorageLessonDataStore : IDataStore<Lesson>
{
    private readonly IDataStore<Subject> _subjectStore = DependencyService.Get<IDataStore<Subject>>();
    private readonly Task _initTask;
    private List<Lesson> _items = new();

    public StorageLessonDataStore()
    {
        _initTask = Task.Run(async () =>
        {
            _items = await Pull();
        });
    }

    public async Task<bool> AddItemAsync(Lesson item)
    {
        await _initTask;
        _items.Add(item);

        return await Push(_items);
    }

    public async Task<bool> DeleteItemAsync(Guid id)
    {
        await _initTask;
        var oldItem = _items.Where(arg => arg.Id == id).FirstOrDefault();
        _items.Remove(oldItem);

        return await Push(_items);
    }

    public async Task<bool> ExistsAsync(Guid id)
    {
        await _initTask;
        return _items.Select(i => i.Id).Contains(id);
    }

    public async Task<Lesson> GetItemAsync(Guid id)
    {
        await _initTask;
        var item = _items.FirstOrDefault(s => s.Id == id);
        return item with
        {
            Subject = await _subjectStore.GetItemAsync(item.Subject.Id)
        };
    }

    public async IAsyncEnumerable<Lesson> GetItemsAsync(bool forceRefresh = false)
    {
        await _initTask;
        if (forceRefresh)
        {
            _items = await Pull();
        }

        foreach (var item in _items.Select<Lesson, ValueTask<Lesson>>(async item =>
        {
            return item with
            {
                Subject = await _subjectStore.GetItemAsync(item.Subject.Id)
            };
        }))
        {
            yield return await item;
        }
    }

    public async Task<bool> UpdateItemAsync(Lesson item)
    {
        await _initTask;
        var oldItem = _items.Where(arg => arg.Id == item.Id).FirstOrDefault();
        _items.Remove(oldItem);
        _items.Add(item);

        return await Push(_items);
    }

    private static async ValueTask<List<Lesson>> Pull()
    {
        return await StorageHelper.Pull("lessons.json", () => new List<Lesson>());
    }

    private static async ValueTask<bool> Push(List<Lesson> data)
    {
        return await StorageHelper.Push("lessons.json", data);
    }
}
