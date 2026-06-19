// ====== ПРОФИЛЬ ======
document.addEventListener('DOMContentLoaded', function() {
    // ПРОВЕРКА АВТОРИЗАЦИИ (без checkAuth)
    const user = JSON.parse(sessionStorage.getItem('currentUser'));
    console.log('🔍 profile.js: пользователь:', user);
    
    if (!user) {
        window.location.href = 'index.html';
        return;
    }

    const nameEl = document.getElementById('profileName');
    const emailEl = document.getElementById('profileEmail');
    const avatarEl = document.getElementById('profileAvatar');
    
    if (nameEl) nameEl.textContent = user.name || 'Автор';
    if (emailEl) emailEl.textContent = user.email || '';
    if (avatarEl) avatarEl.textContent = (user.name || 'А').charAt(0);

    const books = [
        { title: 'Тени прошлого', status: 'Опубликована' },
        { title: 'Новая надежда', status: 'На модерации' },
        { title: 'Черновик 3', status: 'Черновик' }
    ];

    const container = document.getElementById('profileBooks');
    
    if (!container) {
        console.error('❌ Элемент profileBooks не найден!');
        return;
    }
    
    if (books.length === 0) {
        container.innerHTML = `<p style="color: var(--gray);">У вас пока нет книг</p>`;
    } else {
        container.innerHTML = books.map(book => `
            <div style="display: flex; justify-content: space-between; padding: 12px 0; border-bottom: 1px solid #F3F4F6;">
                <span>${book.title}</span>
                <span class="status ${book.status === 'Опубликована' ? 'status-published' : book.status === 'На модерации' ? 'status-moderation' : 'status-draft'}">
                    ${book.status}
                </span>
            </div>
        `).join('');
    }
});

// ====== ФУНКЦИЯ ВЫХОДА ======
function logout() {
    if (confirm('Вы уверены, что хотите выйти?')) {
        sessionStorage.removeItem('currentUser');
        window.location.href = 'index.html';
    }
}