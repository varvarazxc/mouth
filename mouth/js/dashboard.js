// ====== ДАШБОРД С SUPABASE ======
document.addEventListener('DOMContentLoaded', async function() {
    // ПРОВЕРКА АВТОРИЗАЦИИ (без checkAuth)
    const user = JSON.parse(sessionStorage.getItem('currentUser'));
    console.log('🔍 dashboard.js: пользователь:', user);
    
    if (!user) {
        window.location.href = 'index.html';
        return;
    }
    
    // Отображаем имя пользователя
    const nameEl = document.getElementById('userName');
    const emailEl = document.getElementById('userEmail');
    const avatarEl = document.getElementById('userAvatar');
    
    if (nameEl) nameEl.textContent = user.name || 'Автор';
    if (emailEl) emailEl.textContent = user.email || '';
    if (avatarEl) avatarEl.textContent = (user.name || 'А').charAt(0);
    
    try {
        // Проверяем, что supabase существует
        if (typeof supabase === 'undefined') {
            console.error('❌ Supabase не подключен!');
            return;
        }
        
        // Загружаем книги автора
        const { data: books, error } = await supabase
            .from('books')
            .select('*')
            .eq('author_id', user.id);
        
        if (error) throw error;
        
        console.log('📚 Загружено книг:', books?.length || 0);
        
        // Считаем статистику
        const totalBooks = books?.length || 0;
        const publishedBooks = books?.filter(b => b.status === 'published') || [];
        const draftBooks = books?.filter(b => b.status === 'draft') || [];
        const totalViews = books?.reduce((sum, b) => sum + (b.views || 0), 0) || 0;
        const totalLikes = books?.reduce((sum, b) => sum + (b.likes || 0), 0) || 0;
        
        const statBooks = document.getElementById('statBooks');
        const statViews = document.getElementById('statViews');
        const statLikes = document.getElementById('statLikes');
        const statDrafts = document.getElementById('statDrafts');
        
        if (statBooks) statBooks.textContent = totalBooks;
        if (statViews) statViews.textContent = totalViews;
        if (statLikes) statLikes.textContent = totalLikes;
        if (statDrafts) statDrafts.textContent = draftBooks.length;
        
        // Загружаем комментарии к книгам автора
        const bookIds = books?.map(b => b.id) || [];
        if (bookIds.length > 0) {
            const { data: comments } = await supabase
                .from('comments')
                .select('*, profiles(name)')
                .in('book_id', bookIds)
                .order('created_at', { ascending: false })
                .limit(5);
            
            const commentsContainer = document.getElementById('recentComments');
            if (commentsContainer) {
                if (comments && comments.length > 0) {
                    commentsContainer.innerHTML = comments.map(c => `
                        <div style="padding: 12px 0; border-bottom: 1px solid #F3F4F6;">
                            <strong>${c.profiles?.name || 'Аноним'}</strong>
                            <p style="margin-top: 4px; color: #4B5563;">${c.text}</p>
                        </div>
                    `).join('');
                } else {
                    commentsContainer.innerHTML = '<p style="color: var(--gray);">Нет комментариев</p>';
                }
            }
        }
        
    } catch (error) {
        console.error('❌ Ошибка загрузки данных:', error);
    }
});

// ====== ФУНКЦИЯ ВЫХОДА ======
function logout() {
    if (confirm('Вы уверены, что хотите выйти?')) {
        sessionStorage.removeItem('currentUser');
        window.location.href = 'index.html';
    }
}