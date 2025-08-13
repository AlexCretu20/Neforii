using AppCore;

namespace ImageProcessingEntrypoint.Services
{
    public static class FilterService
    {
        public static List<(int Id, string Name, string Label, IFilter Instance)> GetFilters()
        {
            var filters = FilterLoader.LoadAll(PathConfig.FiltersDir);
            Console.WriteLine($"\n Lista de filtre {filters.Count}");
            int index = 0;
            var filterList = new List<(int, string, string, IFilter)>();

            filterList.Add((index++, "none", "fara filtru", null));
            Console.WriteLine(index);
            foreach (var filter in filters)
            {
                var filterType = filter.GetType();
                var nameFilter = filterType.Name;
                var labelFilter = nameFilter;

                filterList.Add((index++, nameFilter, labelFilter, filter));
            

            }
            Console.WriteLine($"Lista de obictee {filterList.Count}");

            return filterList;
        }
    }
    
}