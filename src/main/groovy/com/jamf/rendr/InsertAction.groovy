package com.jamf.rendr

class InsertAction extends Action {
    File file
    String text
    String after

    InsertAction after(String text) {
        this.after = text
        this
    }

    InsertAction into(file) {
        this.file = new File(file)
        this
    }

    void run() {
        file.text = file.text.replace(after, "$after$text")
    }

    void validate() {
        file.exists() ?: toss("Insert failed: file '$file' does not exist")
        file.text.contains(after) ?: toss("Insert failed: file '$file.name' does not contain '${clean(after)}'")
    }

    String toString() {
        "Insert '${clean(text)}' after '${clean(after)}' in $file.name"
    }
}
