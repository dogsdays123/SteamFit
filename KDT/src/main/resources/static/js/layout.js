function toggleMenu(show) {
    document.getElementById('sideMenu').classList.toggle('hidden', !show);
    document.getElementById('overlay').classList.toggle('hidden', !show);
}

function openSearchPage() {
    document.getElementById('searchPage').classList.remove('hidden');
    document.getElementById('searchInput').focus();
}
function closeSearchPage() {
    document.getElementById('searchPage').classList.add('hidden');
    document.getElementById('searchInput').value = '';
    document.getElementById('searchResults').innerHTML = '';
}

function searchGames(query) {
    const results = [
        { title: '엘든링 DLC', steamUrl: 'https://store.steampowered.com/app/1245620', communityId: '123', hasCommunity: true },
        { title: '하데스', steamUrl: 'https://store.steampowered.com/app/1145360', communityId: '456', hasCommunity: true },
        { title: '팔월의 블루', steamUrl: 'https://store.steampowered.com/app/999999', communityId: '999', hasCommunity: false },
    ];
    const filtered = results.filter(g => g.title.includes(query));
    const container = document.getElementById('searchResults');
    container.innerHTML = '';
    filtered.forEach(game => {
        const div = document.createElement('li');
        div.className = 'bg-gray-100 rounded p-3 cursor-pointer hover:bg-gray-200';
        div.innerHTML = `<div class='font-semibold'>🎮 ${game.title}</div><div class='text-sm text-gray-600'>${game.hasCommunity ? '커뮤니티 참여 가능' : '커뮤니티 없음'}</div>`;
        div.onclick = () => {
            closeSearchPage();
            openGameModal(game.title, game.hasCommunity, game.steamUrl, game.communityId);
        };
        container.appendChild(div);
    });
}
function slideLeft(id) {
    document.getElementById(id).scrollBy({ left: -300, behavior: 'smooth' });
}
function slideRight(id) {
    document.getElementById(id).scrollBy({ left: 300, behavior: 'smooth' });
}

// 햄버거 버튼 > 드롭다운 메뉴
function toggleDropdown() {
    const dropdown = document.getElementById('dropdownMenu');
    dropdown.classList.toggle('hidden');
}

// 바깥 클릭 시 닫기
window.addEventListener('click', function (e) {
    const menu = document.getElementById('dropdownMenu');
    const button = document.querySelector('button[onclick="toggleDropdown()"]');
    if (!menu.contains(e.target) && !button.contains(e.target)) {
        menu.classList.add('hidden');
    }
});