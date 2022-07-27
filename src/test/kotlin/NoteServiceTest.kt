import org.junit.Test

import org.junit.Assert.*

class NoteServiceTest {

    //автотест функции добавления заметки
    @Test
    fun add() {
        NoteService.eraseAll()
        val addNote = NoteService.add(1,"Заметка 1","Текст заметки 1", 0,0,"","")
        val addNote2 = NoteService.add(1,"Заметка 2","Текст заметки 2", 0,0,"","")
        assertEquals(1,addNote)
        assertEquals(2,addNote2)
    }

    //автотест функции добавлния комментария
    @Test
    fun createComment() {
        NoteService.eraseAll()
        val addNote = NoteService.add(1,"Заметка 1","Текст заметки 1", 0,0,"","")
        val createComment = NoteService.createComment(1,1,1,"Комментарий 1")
        assertEquals(1,createComment)
    }

    //автотест функции удаления заметки
    @Test
    fun delete() {
        NoteService.eraseAll()
        val addNote = NoteService.add(1,"Заметка 1","Текст заметки 1", 0,0,"","")
        val addNote2 = NoteService.add(1,"Заметка 2","Текст заметки 2", 0,0,"","")
        val deleteNote = NoteService.delete(1,1)
        assertEquals(1,deleteNote)
    }

    //автотест функции удаления заметки(обработка исключений)
    @Test(expected = NotesNotFoundException:: class)
    fun deleteNoteException() {
        NoteService.eraseAll()
        val addNote = NoteService.add(1,"Заметка 1","Текст заметки 1", 0,0,"","")
        val addNote2 = NoteService.add(1,"Заметка 2","Текст заметки 2", 0,0,"","")
        val deleteNote = NoteService.delete(1,10)
    }

    ////автотест функции удаления комментария
    @Test
    fun deleteComment() {
        NoteService.eraseAll()
        val addNote = NoteService.add(1,"Заметка 1","Текст заметки 1", 0,0,"","")
        val createComment = NoteService.createComment(1,1,1,"Комментарий 1")
        val deleteComment = NoteService.deleteComment(1,1)
        assertEquals(1,deleteComment)
    }

    //автотест функции удаления комментария(обработка исключений)
    @Test(expected = CommentsNotFoundException:: class)
    fun deleteCommentCommentsException() {
        NoteService.eraseAll()
        val addNote = NoteService.add(1,"Заметка 1","Текст заметки 1", 0,0,"","")
        val createComment = NoteService.createComment(1,1,1,"Комментарий 1")
        val deleteComment = NoteService.deleteComment(1,3)
    }

    //автотест функции редактирования заметки
    @Test
    fun edit() {
        NoteService.eraseAll()
        NoteService.add(1,"Заметка 1","Текст заметки 1", 0,0,"","")
        val noteEdit = NoteService.edit(1,"Заметка 1 изм.", "Текст изменен",0,0,"","")
        assertEquals(1,noteEdit)
    }

    //автотест функции редактирования заметки(обработка исключений)
    @Test(expected = NotesNotFoundException:: class)
    fun editNotesException() {
        NoteService.eraseAll()
        NoteService.add(1,"Заметка 1","Текст заметки 1", 0,0,"","")
        val noteEdit = NoteService.edit(5,"Заметка 1 изм.", "Текст изменен",0,0,"","")
    }

    //автотест возвращения заметки по id
    @Test
    fun getById() {
        NoteService.eraseAll()
        val note = Notes(1,"Заметка 1","Текст заметки 1", 0,0,"","",id = 1)
        NoteService.add(1,"Заметка 1","Текст заметки 1", 0,0,"","")
        val get = NoteService.getById(1,1)
        assertEquals(note,get)
    }

    //автотест возвращения списка комментариев
    @Test
    fun getComments() {
        NoteService.add(1,"Заметка 1","Текст заметки 1", 0,0,"","")
        NoteService.createComment(1,1,1,"Комментарий 1")
        val comments = Comments(1,1,1,"Комментарий 1")
        val list = mutableListOf<Comments>()
        list.add(comments)
        val getComment = NoteService.getComments(1,1,0,0,0)
        assertEquals(list, getComment)
    }

    //автотест возвращения списка комментариев(обработка исключений)
    @Test(expected = NotesNotFoundException:: class)
    fun getCommentsException() {
        NoteService.add(1,"Заметка 1","Текст заметки 1", 0,0,"","")
        NoteService.createComment(1,1,1,"Комментарий 1")
        val comments = Comments(1,1,1,"Комментарий 1")
        val list = mutableListOf<Comments>()
        list.add(comments)
        val getComment = NoteService.getComments(5,1,0,0,0)
        assertEquals(list, getComment)
    }

}