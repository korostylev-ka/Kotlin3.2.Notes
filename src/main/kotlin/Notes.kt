//класс обработки исключений при указании несуществующей заметки
class NotesNotFoundException(message: String): RuntimeException(message)

//класс обработки исключений при указании несуществующего комментария
class CommentsNotFoundException(message: String): RuntimeException(message)

//класс обработки исключений при указании несуществующего пользователя
class UserNotFoundException(message: String): RuntimeException(message)

data class Notes(
    val userId: Int, //id пользователя
    var title: String, //Заголовок заметки
    var text: String, //Текст заметки
    var privacy: Int, //Уровень доступа к заметке
    var commentPrivacy: Int, //Уровень доступа к комментированию заметки
    var privacyView: String, //Настройки приватности просмотра заметки
    var privacyComment: String, //Настройки приватности комментирования заметки
    var id: Int = 0
) {

}

data class Comments(
    val noteId: Int, //Идентификатор заметки
    val ownerId: Int, //Идентификатор владельца заметки
    val replyTo: Int, //Идентификатор пользователя, ответом на комментарий которого является добавляемый комментарий
    val message: String, //Текст комментария

) {
    var id: Int = 0 //id комментария
    //Уникальный идентификатор, предназначенный для предотвращения повторной отправки одинакового комментария
    var guid: String = noteId.toString() + ownerId.toString() + message
}

object NoteService {
    //уникальный идентификатор заметок
    var noteId = 0
    //уникальный идентификатор комментариев
    var commentsId = 0
    //коллекция заметок, где ключ - id пользователя
    var listOfNotes = mutableMapOf<Int, MutableList<Notes>?>()
    //коллекция комментариев, где ключ - id заметки, которую комментировали
    var listOfComments = mutableMapOf<Int, MutableList<Comments>?>()

    //метод очистки полей для автотестов
    fun eraseAll(): Unit{
        noteId = 0
        commentsId = 0
        listOfComments.clear()
        listOfNotes.clear()
    }

    //Создает новую заметку у текущего пользователя
    fun add(userId: Int,
            title: String,
            text: String,
            privacy: Int,
            commentPrivacy: Int,
            privacyView: String,
            privacyComment: String
    ): Int {
        val note = Notes(userId,title,text,privacy,commentPrivacy,privacyView,privacyComment)
        val listNotes: MutableList<Notes>?
        //оператор Элвиса для проверки есть ли уже список для данного id
        listNotes = listOfNotes.get(userId) ?: mutableListOf<Notes>()
        listNotes?.add(note)
        note.id = ++noteId
        listOfNotes.put(userId,listNotes)
        return note.id
    }

    //создает комментарий к заметке
    fun createComment(noteId: Int,
                      ownerId: Int,
                      replyTo: Int,
                      message: String
    ): Int{
        //проверяем есть ли id автора
        var isExist = false
        if (listOfNotes.containsKey(ownerId)) {
            //проверяем есть ли id заметки
            for (notes in listOfNotes.get(ownerId)!!) {
                if (notes.id == noteId) {
                    val comment = Comments(noteId,ownerId,replyTo,message)
                    //оператор Элвиса для проверки есть ли комментарии у этой заметки
                    val listComments = listOfComments?.get(noteId) ?: mutableListOf<Comments>()
                    listComments.add(comment)
                    comment.id = ++commentsId
                    listOfComments.put(noteId, listComments)
                    isExist = true
                }
            }
        }
        return commentsId
    }

    //Удаляет заметку текущего пользователя(также удаляются все комментарии к ней).
    fun delete(ownerId: Int,
               noteId: Int,
    ): Int {
        //существует ли такой пользователь
        var isNoteExist = false
        //существует ли такая заметка
        var isUserExist = false
        //итератор по списку
        val iterator = listOfNotes.iterator()
        while(iterator.hasNext()) {
            val note = iterator.next()
            //проверяем есть ли записи у данного пользователя
            if (note.key == ownerId) {
                isUserExist = true
                val iteratorNotes = note.value?.iterator()
                //проверяем на наличие записи с данным id
                while (iteratorNotes?.hasNext()!!) {
                    val notes = iteratorNotes.next()
                    if (notes.id == noteId) {
                        isNoteExist = true
                        iteratorNotes.remove()
                        //удаляем все комментарии у удаленной записи
                        val commentsIterator = listOfComments.iterator()
                        while (commentsIterator.hasNext()) {
                            val comment = commentsIterator.next()
                            if (comment.key == noteId) {
                                commentsIterator.remove()
                            }
                        }
                    }
                }
            }
        }
        //выкидываем исключение, если пользователя с данным id нет
        if (isUserExist == false) throw UserNotFoundException("Пользователя с данным id = $ownerId нет")
        //выкидываем исключение, если заметки с нужным id нет
        if (isNoteExist == false) throw NotesNotFoundException("Заметки с данным id = $noteId нет")
        return 1
    }

    //Удаляет комментарий к заметке
    fun deleteComment(ownerId: Int,
                      commentId: Int
    ): Int {
        //существует ли такой комментарий
        var isCommentExist = false
        //итератор по списку
        val iterator = listOfComments.iterator()
        while (iterator.hasNext()) {
            val comment = iterator.next()
            val iteratorComments = comment.value?.iterator()
            while (iteratorComments?.hasNext()!!) {
                val comment = iteratorComments.next()
                if (comment.id == commentId && comment.ownerId == ownerId){
                    iteratorComments.remove()
                    isCommentExist = true
                }
            }
            //выкидываем исключение, если комментария с нужным id нет
            if (isCommentExist == false) throw CommentsNotFoundException("Комментария с данным id = $noteId нет")
        }
        return 1
    }

    //Редактирует заметку текущего пользователя
    fun edit(noteId: Int,
            title: String,
            text: String,
            privacy: Int,
            commentPrivacy: Int,
            privacyView: String,
            privacyComment: String
    ): Int {
        //проверка на существование данной заметки
        var isExist = false
        val iterator = listOfNotes.iterator()
        while (iterator.hasNext()) {
            val note = iterator.next()
            val iteratorNotes = note.value?.iterator()
            while (iteratorNotes?.hasNext()!!) {
                val notes = iteratorNotes.next()
                //если id заметки совпадает с указанным при вызове функции
                if (notes.id == noteId) {
                    isExist = true
                    notes.title = title
                    notes.text = text
                    notes.privacy = privacy
                    notes.commentPrivacy = commentPrivacy
                    notes.privacyView = privacyView
                    notes.privacyComment = privacyComment
                }
            }
        }
        //выбрасываем исключение, если нет заметки с данным id
        if (isExist == false) throw NotesNotFoundException("Ошибка 180. Такой заметки не существует!")
        return 1
    }

    //Возвращает заметку по её id
    fun getById(
        noteId: Int,
        ownerId: Int,
    ): Notes? {
        var isExist = false
        var isUser = false
        var getNotes: Notes? = null
        val iterator = listOfNotes.iterator()
        while (iterator.hasNext()) {
            val note = iterator.next()
            val iteratorNotes = note.value?.iterator()
            while (iteratorNotes?.hasNext()!!) {
                val notes = iteratorNotes.next()
                //если id заметки совпадает с указанным при вызове функции
                if (notes.id == noteId && notes.userId == ownerId) {
                    isExist = true
                    isUser = true
                    getNotes = notes
                }
            }
        }
        if (isExist == false || isUser == false) throw NotesNotFoundException("Ошибка 180. Такой заметки нет!")
        return getNotes
    }

    //Возвращает список комментариев к заметке
    fun getComments(
        noteId: Int,
        ownerId: Int,
        sort: Int,
        offset: Int,
        count: Int
    ): MutableList<Comments>? {
        var isComment = false
        var isUser = false
        var list: MutableList<Comments>? = null
        val iterator = listOfComments.iterator()
        while (iterator.hasNext()) {
            val comment = iterator.next()
            if (comment.key == noteId) {
                isComment = true
                list = comment.value
                val listIterator = list?.iterator()
                while (listIterator?.hasNext()!!) {
                    val comments = listIterator.next()
                    if (comments.ownerId == ownerId) {
                        isUser = true
                    }
                }
            }
        }
        if (isComment == false) throw NotesNotFoundException("Такой заметки нет")
        if (isUser == false) throw UserNotFoundException("Такого пользователя нет!")
        return list
    }

}