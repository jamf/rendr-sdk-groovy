package com.jamf.rendr

class Dir {
    @Delegate
    File file

    Dir(path) {
        file = new File(path)
    }

    Dir(File parent, path) {
        file = new File(parent, path)
    }
}
