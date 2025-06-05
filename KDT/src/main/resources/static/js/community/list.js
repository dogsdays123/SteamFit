function toggleSearch() {
    const box = document.getElementById('searchBox');
    box.classList.toggle('opacity-0');
    box.classList.toggle('invisible');
    box.classList.toggle('translate-y-4');
}

function toggleCategoryModal() {
    document.getElementById("categoryModal").classList.toggle("hidden");
}