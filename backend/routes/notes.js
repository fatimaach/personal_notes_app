const express = require("express");
const Note = require("../models/Note");
const auth = require("../middleware/authMiddleware");
const router = express.Router();

router.post("/", auth, async (req, res) => {
    const note = await Note.create({ user: req.user, content: req.body.content });
    res.json(note);
});

router.get("/", auth, async (req, res) => {
    const notes = await Note.find({ user: req.user });
    res.json(notes);
});

router.delete("/:id", auth, async (req, res) => {
    await Note.findByIdAndDelete(req.params.id);
    res.json({ msg: "Note deleted" });
});

module.exports = router;
