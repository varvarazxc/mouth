// ====== СПИСОК КНИГ С АВТООБНОВЛЕНИЕМ ======
document.addEventListener('DOMContentLoaded', function() {
    const user = JSON.parse(sessionStorage.getItem('currentUser'));
    if (!user) {
        window.location.href = 'index.html';
        return;
    }

    const supabase = window.supabaseClient;
    if (!supabase) {
        alert('❌ Ошибка: Supabase не подключен!');
        return;
    }

    let books = [];
    let deleteId = null;

    // ====== ЗАГРУЗКА КНИГ ======
    async function loadBooks() {
        try {
            console.log('📡 Загружаем книги...');
            
            const { data, error } = await supabase
                .from('books')
                .select('*')
                .eq('author_id', user.id)
                .order('created_at', { ascending: false });
            
            if (error) throw error;
            
            books = data || [];
            console.log('📚 Загружено книг:', books.length);
            renderBooks();
            
        } catch (error) {
            console.error('❌ Ошибка загрузки:', error);
        }
    }

    // ====== ОТОБРАЖЕНИЕ КНИГ ======
    function renderBooks() {
        const tbody = document.getElementById('booksTableBody');
        if (!tbody) return;
        
        if (books.length === 0) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="4" style="text-align:center;padding:40px;color:var(--gray);">
                        📭 У вас пока нет книг. Создайте первую!
                    </td>
                </tr>
            `;
            return;
        }
        
        tbody.innerHTML = books.map(book => `
            <tr>
                <td><strong>${book.title || 'Без названия'}</strong></td>
                <td>${new Date(book.created_at).toLocaleDateString('ru-RU')}</td>
                <td><span class="status status-${book.status}">${getStatusText(book.status)}</span></td>
                <td>
                    <div class="actions">
                        <button class="btn-edit" onclick="editBook(${book.id})">✏️</button>
                        <button class="btn-delete" onclick="showDeleteModal(${book.id})">🗑️</button>
                    </div>
                </td>
            </tr>
        `).join('');
    }

    function getStatusText(status) {
        const map = {
            'published': '✅ Опубликована',
            'moderation': '⏳ На модерации',
            'draft': '📝 Черновик'
        };
        return map[status] || status;
    }

    // ====== РЕДАКТИРОВАТЬ ======
    window.editBook = function(id) {
        window.location.href = `editor.html?id=${id}`;
    };

    // ====== УДАЛИТЬ ======
    window.showDeleteModal = function(id) {
        const book = books.find(b => b.id === id);
        if (book) {
            deleteId = id;
            document.getElementById('deleteBookTitle').textContent = book.title || 'Без названия';
            document.getElementById('deleteModal').classList.add('show');
        }
    };

    window.closeModal = function() {
        document.getElementById('deleteModal').classList.remove('show');
        deleteId = null;
    };

    document.getElementById('confirmDeleteBtn').addEventListener('click', async function() {
        if (deleteId !== null) {
            try {
                await supabase.from('books').delete().eq('id', deleteId);
                books = books.filter(b => b.id !== deleteId);
                renderBooks();
                closeModal();
                showToast('🗑️ Книга удалена!');
            } catch (error) {
                showToast('❌ Ошибка удаления');
            }
        }
    });

    function showToast(message) {
        const toast = document.createElement('div');
        toast.className = 'toast show';
        toast.textContent = message;
        document.body.appendChild(toast);
        setTimeout(() => {
            toast.classList.remove('show');
            setTimeout(() => toast.remove(), 300);
        }, 3000);
    }

    // ====== АВТООБНОВЛЕНИЕ КАЖДЫЕ 3 СЕКУНДЫ ======
    loadBooks();
    setInterval(loadBooks, 3000);
});

// ====== ВЫХОД ======
function logout() {
    if (confirm('Выйти?')) {
        sessionStorage.removeItem('currentUser');
        window.location.href = 'index.html';
    }
}