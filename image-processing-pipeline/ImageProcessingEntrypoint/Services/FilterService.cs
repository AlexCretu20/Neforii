using AppCore;

namespace ImageProcessingEntrypoint.Services
{
    public static class FilterService
    {
        public static List<(int Id, string Name, string Label, IFilter? Instance)> GetFilters()
        {
            var filters = FilterLoader.LoadAll(PathConfig.FiltersDir);
            int index = 0;

            var filterList = new List<(int, string, string, IFilter?)>();
            filterList.Add((index++, "none", "fara filtru", null));

            foreach (var filter in filters)
            {
                var name = filter.GetType().Name;
                filterList.Add((index++, name, name, filter));
            }

            return filterList;
        }
    }
}
