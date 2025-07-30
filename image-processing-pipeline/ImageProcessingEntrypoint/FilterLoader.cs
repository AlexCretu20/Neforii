using System.Reflection;
using AppCore;

namespace ImageProcessingEntrypoint;

public static class FilterLoader
{
    public static List<IFilter> LoadAll(string filtersDir)
    {
        return Directory.GetFiles(filtersDir, "*.dll")
            .Select(Assembly.LoadFrom)
            .SelectMany(a => a.GetTypes())
            .Where(t => typeof(IFilter).IsAssignableFrom(t) && !t.IsAbstract && !t.IsInterface)
            .Select(t => (IFilter)Activator.CreateInstance(t)!)
            .ToList();
    }
}