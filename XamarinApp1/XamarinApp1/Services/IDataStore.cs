using System;
using System.Collections.Generic;
using System.Threading.Tasks;

namespace XamarinApp1.Services;

public interface IDataStore<T>
{
    Task<bool> AddItemAsync(T item);

    Task<bool> UpdateItemAsync(T item);

    Task<bool> DeleteItemAsync(Guid id);

    Task<bool> ExistsAsync(Guid id);

    Task<T> GetItemAsync(Guid id);

    IAsyncEnumerable<T> GetItemsAsync(bool forceRefresh = false);
}
