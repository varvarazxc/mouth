// ====== АДМИН-ПАНЕЛЬ ======
document.addEventListener('DOMContentLoaded', function() {
    const user = JSON.parse(sessionStorage.getItem('currentUser'));
    if (!user) {
        window.location.href = 'index.html';
        return;
    }

    const supabase = window.supabaseClient;
    if (!supabase) {
        alert('Ошибка: Supabase не подключен!');
        return;
    }

    async function loadBooks() {
        try {
            console.log('📡 Загружаем книги на модерации...');
            
            const { data: books, error } = await supabase
                .from('books')
                .select('*')
                .eq('status', 'moderation');
            
            if (error) throw error;
            
            console.log('📚 Найдено книг:', books?.length || 0);
            renderBooks(books || []);
            
        } catch (error) {
            console.error('❌ Ошибка загрузки:', error);
            document.getElementById('moderationList').innerHTML = `
                <div style="text-align:center;padding:40px;color:red;">
                    <h3>❌ Ошибка</h3>
                    <p>${error.message}</p>
                </div>
            `;
        }
    }

    function renderBooks(books) {
        const container = document.getElementById('moderationList');
        const count = document.getElementById('moderationCount');
        
        if (books.length === 0) {
            container.innerHTML = `
                <div style="text-align:center;padding:60px;background:white;border-radius:12px;">
                    <h2 style="font-size:48px;">🎉</h2>
                    <h3>Нет книг на модерации</h3>
                </div>
            `;
            if (count) count.textContent = '0 книг';
            return;
        }

        if (count) count.textContent = books.length + ' книг на модерации';

        container.innerHTML = books.map(book => `
            <div style="background:white;padding:20px;border-radius:12px;margin-bottom:16px;box-shadow:0 2px 8px rgba(0,0,0,0.1);">
                <h3 style="margin:0 0 8px 0;">${book.title || 'Без названия'}</h3>
                <p style="margin:0 0 4px 0;color:#666;">${book.description || 'Нет описания'}</p>
                <p style="margin:0 0 12px 0;color:#999;font-size:12px;">📅 ${new Date(book.created_at).toLocaleDateString('ru-RU')}</p>
                <div style="display:flex;gap:8px;">
                    <button onclick="approveBook('${book.id}')" style="flex:1;background:#10B981;color:white;border:none;padding:10px;border-radius:6px;cursor:pointer;">✅ Одобрить</button>
                    <button onclick="rejectBook('${book.id}')" style="flex:1;background:#EF4444;color:white;border:none;padding:10px;border-radius:6px;cursor:pointer;">❌ Отклонить</button>
                </div>
            </div>
        `).join('');
    }

    // ====== ОДОБРИТЬ ======
    window.approveBook = async function(bookId) {
        if (!confirm('✅ Одобрить эту книгу?')) return;
        try {
            console.log('📡 Одобряем книгу:', bookId);
            
            const { error } = await supabase
                .from('books')
                .update({ status: 'published' })
                .eq('id', bookId);
            
            if (error) throw error;
            
            console.log('✅ Книга одобрена!');
            alert('✅ Книга одобрена и опубликована!');
            loadBooks(); // Обновляем список
            
        } catch (error) {
            console.error('❌ Ошибка:', error);
            alert('❌ Ошибка: ' + error.message);
        }
    };

    // ====== ОТКЛОНИТЬ ======
    window.rejectBook = async function(bookId) {
        const reason = prompt('❌ Причина отклонения:');
        if (reason === null) return;
        try {
            console.log('📡 Отклоняем книгу:', bookId);
            console.log('📝 Причина:', reason);
            
            const { error } = await supabase
                .from('books')
                .update({ status: 'draft' })
                .eq('id', bookId);
            
            if (error) throw error;
            
            console.log('❌ Книга отклонена!');
            alert('❌ Книга отклонена! Причина: ' + reason);
            loadBooks(); // Обновляем список
            
        } catch (error) {
            console.error('❌ Ошибка:', error);
            alert('❌ Ошибка: ' + error.message);
        }
    };

    window.logout = function() {
        if (confirm('Выйти?')) {
            sessionStorage.removeItem('currentUser');
            window.location.href = 'index.html';
        }
    };

    loadBooks();
});