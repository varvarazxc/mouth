// ====== РЕДАКТОР (МАКСИМАЛЬНО ПРОСТО) ======
document.addEventListener('DOMContentLoaded', function() {
    const user = JSON.parse(sessionStorage.getItem('currentUser'));
    if (!user) {
        window.location.href = 'index.html';
        return;
    }

    // БЕРЁМ SUPABASE ИЗ ГЛОБАЛЬНОЙ ПЕРЕМЕННОЙ
    const supabase = window.supabaseClient;
    if (!supabase) {
        alert('❌ Ошибка: Supabase не подключен!');
        return;
    }

    let currentBookId = null;

    // ====== СОХРАНИТЬ КНИГУ ======
    window.saveBook = async function(status) {
        const title = document.getElementById('bookTitle').value.trim();
        const description = document.getElementById('bookDescription').value.trim();
        
        if (!title) {
            alert('⚠️ Введите название книги!');
            return;
        }

        try {
            console.log('📡 Сохраняем книгу...');
            console.log('📝 Название:', title);
            console.log('📝 Статус:', status);

            // ПРОСТО СОХРАНЯЕМ В БД
            const { data, error } = await supabase
                .from('books')
                .insert({
                    title: title,
                    description: description || 'Нет описания',
                    author_id: user.id,
                    status: status
                })
                .select();

            if (error) throw error;

            console.log('✅ Книга сохранена!');
            console.log('📊 Данные:', data);

            if (data && data.length > 0) {
                currentBookId = data[0].id;
                alert('✅ Книга сохранена в БД! ID: ' + currentBookId);
                // Обновляем URL
                window.history.pushState({}, '', `editor.html?id=${currentBookId}`);
            }

        } catch (error) {
            console.error('❌ Ошибка:', error);
            alert('❌ Ошибка: ' + error.message);
        }
    };

    // ====== КНОПКИ ======
    window.saveDraft = function() {
        saveBook('draft');
    };

    window.publishBook = function() {
        saveBook('moderation');
    };

    // ====== ЗАГРУЗКА КНИГИ (если редактируем) ======
    const urlParams = new URLSearchParams(window.location.search);
    const bookId = urlParams.get('id');
    
    if (bookId) {
        currentBookId = bookId;
        console.log('📖 Редактируем книгу:', bookId);
        // TODO: загрузить данные книги
    }

    console.log('✅ Редактор готов!');
});

// ====== ВЫХОД ======
function logout() {
    if (confirm('Выйти?')) {
        sessionStorage.removeItem('currentUser');
        window.location.href = 'index.html';
    }
}