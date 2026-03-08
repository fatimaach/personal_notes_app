const express = require("express");
const Note = require("../models/Note");
const auth = require("../middleware/authMiddleware");

const router = express.Router();

// GET all notes for logged-in user
router.get("/", auth, async (req, res) => {
  try {
    const notes = await Note.find({ user: req.user }).sort({ createdAt: -1 });
    res.json(notes);
  } catch (error) {
    console.error(error.message);
    res.status(500).json({ msg: "Server error" });
  }
});

// ADD a new note
router.post("/", auth, async (req, res) => {
  try {
    const { title, content } = req.body;

    if (!title || !content) {
      return res.status(400).json({ msg: "Title and content are required" });
    }

    const note = await Note.create({
      user: req.user,
      title: title.trim(),
      content: content.trim()
    });

    res.status(201).json(note);
  } catch (error) {
    console.error(error.message);
    res.status(500).json({ msg: "Server error" });
  }
});

// UPDATE a note
router.put("/:id", auth, async (req, res) => {
  try {
    const { title, content } = req.body;

    let note = await Note.findOne({
      _id: req.params.id,
      user: req.user
    });

    if (!note) {
      return res.status(404).json({ msg: "Note not found" });
    }

    note.title = title ? title.trim() : note.title;
    note.content = content ? content.trim() : note.content;

    await note.save();
    res.json(note);
  } catch (error) {
    console.error(error.message);
    res.status(500).json({ msg: "Server error" });
  }
});

// DELETE a note
router.delete("/:id", auth, async (req, res) => {
  try {
    const note = await Note.findOneAndDelete({
      _id: req.params.id,
      user: req.user
    });

    if (!note) {
      return res.status(404).json({ msg: "Note not found" });
    }

    res.json({ msg: "Note deleted successfully" });
  } catch (error) {
    console.error(error.message);
    res.status(500).json({ msg: "Server error" });
  }
});

module.exports = router;