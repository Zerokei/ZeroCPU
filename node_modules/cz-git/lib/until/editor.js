"use strict";
var __read = (this && this.__read) || function (o, n) {
    var m = typeof Symbol === "function" && o[Symbol.iterator];
    if (!m) return o;
    var i = m.call(o), r, ar = [], e;
    try {
        while ((n === void 0 || n-- > 0) && !(r = i.next()).done) ar.push(r.value);
    }
    catch (error) { e = { error: error }; }
    finally {
        try {
            if (r && !r.done && (m = i["return"])) m.call(i);
        }
        finally { if (e) throw e.error; }
    }
    return ar;
};
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.getPreparedCommit = exports.editCommit = void 0;
var fs_1 = __importDefault(require("fs"));
var path_1 = __importDefault(require("path"));
var os_1 = __importDefault(require("os"));
var constants_1 = __importDefault(require("constants"));
var rimraf_1 = __importDefault(require("rimraf"));
var child_process_1 = require("child_process");
var until_1 = require("./until");
var dir = path_1.default.resolve(os_1.default.tmpdir());
var RDWR_EXCL = constants_1.default.O_CREAT | constants_1.default.O_TRUNC | constants_1.default.O_RDWR | constants_1.default.O_EXCL;
var dirsToDelete = [];
var rimrafSync = rimraf_1.default.sync;
var promisify = function (callback) {
    var arges = [];
    for (var _i = 1; _i < arguments.length; _i++) {
        arges[_i - 1] = arguments[_i];
    }
    if (typeof callback === "function") {
        return [undefined, callback];
    }
    var promiseCallback;
    var promise = new Promise(function (resolve, reject) {
        promiseCallback = function () {
            var args = Array.from(arges);
            var err = args.shift();
            process.nextTick(function () {
                if (err) {
                    reject(err);
                }
                else if (args.length === 1) {
                    resolve(args[0]);
                }
                else {
                    resolve(args);
                }
            });
        };
    });
    return [promise, promiseCallback];
};
var parseAffixes = function (rawAffixes, defaultPrefix) {
    var affixes = { prefix: null, suffix: null };
    if (rawAffixes) {
        switch (typeof rawAffixes) {
            case "string":
                affixes.prefix = rawAffixes;
                break;
            case "object":
                affixes = rawAffixes;
                break;
            default:
                throw new Error("Unknown affix declaration: " + affixes);
        }
    }
    else {
        affixes.prefix = defaultPrefix;
    }
    return affixes;
};
var generateName = function (rawAffixes, defaultPrefix) {
    var affixes = parseAffixes(rawAffixes, defaultPrefix);
    var now = new Date();
    var name = [
        affixes.prefix,
        now.getFullYear(),
        now.getMonth(),
        now.getDate(),
        "-",
        process.pid,
        "-",
        (Math.random() * 0x100000000 + 1).toString(36),
        affixes.suffix
    ].join("");
    return path_1.default.join(affixes.dir || dir, name);
};
function cleanupFilesSync() {
    if (!tracking) {
        return false;
    }
    var count = 0;
    var toDelete;
    while ((toDelete = filesToDelete.shift()) !== undefined) {
        rimrafSync(toDelete, { maxBusyTries: 6 });
        count++;
    }
    return count;
}
function cleanupDirsSync() {
    if (!tracking) {
        return false;
    }
    var count = 0;
    var toDelete;
    while ((toDelete = dirsToDelete.shift()) !== undefined) {
        rimrafSync(toDelete, { maxBusyTries: 6 });
        count++;
    }
    return count;
}
function cleanupSync() {
    if (!tracking) {
        return false;
    }
    var fileCount = cleanupFilesSync();
    var dirCount = cleanupDirsSync();
    return { files: fileCount, dirs: dirCount };
}
var tracking = false;
var exitListenerAttached = false;
function attachExitListener() {
    if (!tracking)
        return false;
    if (!exitListenerAttached) {
        process.addListener("exit", function () {
            try {
                cleanupSync();
            }
            catch (err) {
                console.warn("Fail to clean temporary files on exit : ", err);
                throw err;
            }
        });
        exitListenerAttached = true;
    }
}
var filesToDelete = [];
function deleteFileOnExit(filePath) {
    if (!tracking)
        return false;
    attachExitListener();
    filesToDelete.push(filePath);
}
var tempOpen = function (affixes, callback) {
    var p = promisify(callback);
    var promise = p[0];
    callback = p[1];
    var path = generateName(affixes, "f-");
    fs_1.default.open(path, RDWR_EXCL, 384, function (err, fd) {
        if (!err) {
            deleteFileOnExit(path);
        }
        callback(err, { path: path, fd: fd });
    });
    return promise;
};
/**
 * @description: fork by "editor" v1.0.0
 */
var editor = function (file, opts, cb) {
    if (typeof opts === "function") {
        cb = opts;
        opts = {};
    }
    if (!opts)
        opts = {};
    var ed = /^win/.test(process.platform) ? "notepad" : "vim";
    var editor = opts.editor || process.env.VISUAL || process.env.EDITOR || ed;
    var args = editor.split(/\s+/);
    var bin = args.shift();
    var ps = (0, child_process_1.spawn)(bin, args.concat([file]), { stdio: "inherit" });
    ps.on("exit", function (code, sig) {
        if (typeof cb === "function")
            cb(code, sig);
    });
};
var editCommit = function (answers, options, cb) {
    tempOpen(undefined, function (err, info) {
        if (!err) {
            fs_1.default.writeSync(info.fd, (0, until_1.buildCommit)(answers, options));
            fs_1.default.close(info.fd, function () {
                editor(info.path, function (code) {
                    if (code === 0) {
                        var commitStr = fs_1.default.readFileSync(info.path, {
                            encoding: "utf8"
                        });
                        cb(commitStr);
                    }
                    else {
                        (0, until_1.log)("warm", "Editor exit non zero. Commit message was:\n".concat((0, until_1.buildCommit)(answers, options)));
                    }
                });
            });
        }
    });
};
exports.editCommit = editCommit;
var getPreparedCommit = function (context) {
    var _a;
    var message = null;
    if (fs_1.default.existsSync(path_1.default.resolve(__dirname, "./.git/COMMIT_EDITMSG"))) {
        var prepared = fs_1.default.readFileSync(path_1.default.resolve(__dirname, "./.git/COMMIT_EDITMSG"), "utf-8");
        var preparedCommit = prepared
            .replace(/^#.*/gm, "")
            .replace(/^\s*[\r\n]/gm, "")
            .replace(/[\r\n]$/, "")
            .split(/\r\n|\r|\n/);
        if (preparedCommit.length && preparedCommit[0]) {
            if (context === "subject")
                _a = __read(preparedCommit, 1), message = _a[0];
            else if (context === "body" && preparedCommit.length > 1) {
                preparedCommit.shift();
                message = preparedCommit.join("|");
            }
        }
    }
    return message;
};
exports.getPreparedCommit = getPreparedCommit;
//# sourceMappingURL=editor.js.map