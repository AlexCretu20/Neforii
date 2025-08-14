// using System.Reflection;
// using AppCore;

// namespace ImageProcessingEntrypoint;

// public static class FilterLoader
// {
//     public static List<IFilter> LoadAll(string filtersDir)
//     {
//         return Directory.GetFiles(filtersDir, "*.dll")
//             .Select(Assembly.LoadFrom)
//             .SelectMany(a => a.GetTypes())
//             .Where(t => typeof(IFilter).IsAssignableFrom(t) && !t.IsAbstract && !t.IsInterface)
//             .Select(t => (IFilter)Activator.CreateInstance(t)!)
//             .ToList();
//     }
// }




using System.Reflection;
using System.Runtime.Loader;
using AppCore;

namespace ImageProcessingEntrypoint;

public static class FilterLoader
{
    public static List<IFilter> LoadAll(string pluginsDir)
    {
        var result = new List<IFilter>();

        if (string.IsNullOrWhiteSpace(pluginsDir) || !Directory.Exists(pluginsDir))
            return result;

        foreach (var dll in Directory.EnumerateFiles(pluginsDir, "*.dll", SearchOption.TopDirectoryOnly))
        {
            try
            {
                // Mai robust în Docker/Linux decât Assembly.LoadFrom
                var asm = AssemblyLoadContext.Default.LoadFromAssemblyPath(Path.GetFullPath(dll));

                var types = asm.GetTypes()
                    .Where(t =>
                        typeof(IFilter).IsAssignableFrom(t) &&
                        !t.IsAbstract &&
                        !t.IsInterface &&
                        t.GetConstructor(Type.EmptyTypes) != null);

                foreach (var t in types)
                {
                    if (Activator.CreateInstance(t) is IFilter filter)
                        result.Add(filter);
                }
            }
            catch (Exception ex)
            {
                Console.Error.WriteLine($"[FilterLoader] Failed to load '{dll}': {ex.Message}");
            }
        }

        return result;
    }
}
