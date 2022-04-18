"use strict";
var __importDefault = (this && this.__importDefault) || function (mod) {
    return (mod && mod.__esModule) ? mod : { "default": mod };
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.commitizenConfigLoader = void 0;
/**
 * @description: fork by "@cz-cli" v4.2.4
 */
var fs_1 = __importDefault(require("fs"));
var path_1 = __importDefault(require("path"));
var glob_1 = __importDefault(require("glob"));
var strip_json_comments_1 = __importDefault(require("strip-json-comments"));
// Configuration sources in priority order.
var configs = [".czrc", ".cz.json", "package.json"];
function commitizenConfigLoader(config, cwd) {
    return loader(configs, config, cwd);
}
exports.commitizenConfigLoader = commitizenConfigLoader;
/**
 * Command line config helpers
 * Shamelessly ripped from with slight modifications:
 * https://github.com/jscs-dev/node-jscs/blob/master/lib/cli-config.js
 */
/**
 * Get content of the configuration file
 * @param {String} config - partial path to configuration file
 * @param {String} [cwd = process.cwd()] - directory path which will be joined with config argument
 * @return {Object|undefined}
 */
function loader(configs, config, cwd) {
    var directory = cwd || process.cwd();
    // If config option is given, attempt to load it
    if (config) {
        return getContent(config, directory);
    }
    var content = getContent(findup(configs, { nocase: true, cwd: directory }, function (configPath) {
        if (path_1.default.basename(configPath) === "package.json") {
            // return !!this.getContent(configPath);
        }
        return true;
    }));
    if (content) {
        return content;
    }
    /* istanbul ignore if */
    if (!isInTest()) {
        // Try to load standard configs from home dir
        var directoryArr = [process.env.USERPROFILE, process.env.HOMEPATH, process.env.HOME];
        for (var i = 0, dirLen = directoryArr.length; i < dirLen; i++) {
            if (!directoryArr[i]) {
                continue;
            }
            for (var j = 0, len = configs.length; j < len; j++) {
                content = getContent(configs[j], directoryArr[i]);
                if (content) {
                    return content;
                }
            }
        }
    }
}
// Before, "findup-sync" package was used,
// but it does not provide filter callback
function findup(patterns, options, fn) {
    /* jshint -W083 */
    var lastpath;
    var file;
    options = Object.create(options);
    options.maxDepth = 1;
    options.cwd = path_1.default.resolve(options.cwd);
    do {
        file = patterns.filter(function (pattern) {
            var configPath = glob_1.default.sync(pattern, options)[0];
            if (configPath) {
                return fn(path_1.default.join(options.cwd, configPath));
            }
        })[0];
        if (file) {
            return path_1.default.join(options.cwd, file);
        }
        lastpath = options.cwd;
        options.cwd = path_1.default.resolve(options.cwd, "..");
    } while (options.cwd !== lastpath);
}
/**
 * Get content of the configuration file
 * @param {String} configPath - partial path to configuration file
 * @param {String} directory - directory path which will be joined with config argument
 * @return {Object}
 */
function getContent(configPath, baseDirectory) {
    if (!configPath) {
        return;
    }
    var resolvedPath = path_1.default.resolve(baseDirectory || "", configPath);
    var configBasename = path_1.default.basename(resolvedPath);
    if (!fs_1.default.existsSync(resolvedPath)) {
        return getNormalizedConfig(resolvedPath);
    }
    var content = readConfigContent(resolvedPath);
    return getNormalizedConfig(configBasename, content);
}
function getNormalizedConfig(config, content) {
    if (content && config === "package.json") {
        // PACKAGE.JSON
        // Use the npm config key, be good citizens
        if (content.config && content.config.commitizen) {
            return content.config.commitizen;
        }
        else if (content.czConfig) {
            // Old method, will be deprecated in 3.0.0
            // Suppress during test
            if (typeof global.it !== "function") {
                console.error('\n********\nWARNING: This repository\'s package.json is using czConfig. czConfig will be deprecated in Commitizen 3. \nPlease use this instead:\n{\n  "config": {\n    "commitizen": {\n      "path": "./path/to/adapter"\n    }\n  }\n}\nFor more information, see: http://commitizen.github.io/cz-cli/\n********\n');
            }
            return content.czConfig;
        }
    }
    else {
        // .cz.json or .czrc
        return content;
    }
}
/**
 * Read the content of a configuration file
 * - if not js or json: strip any comments
 * - if js or json: require it
 * @param {String} configPath - full path to configuration file
 * @return {Object}
 */
function readConfigContent(configPath) {
    var parsedPath = path_1.default.parse(configPath);
    var isRcFile = parsedPath.ext !== ".js" && parsedPath.ext !== ".json";
    var jsonString = readConfigFileContent(configPath);
    var parse = isRcFile
        ? function (contents) { return JSON.parse((0, strip_json_comments_1.default)(contents)); }
        : function (contents) { return JSON.parse(contents); };
    try {
        var parsed = parse(jsonString);
        Object.defineProperty(parsed, "configPath", {
            value: configPath
        });
        return parsed;
    }
    catch (error) {
        error.message = [
            "Parsing JSON at ".concat(configPath, " for commitizen config failed:"),
            error.mesasge
        ].join("\n");
        throw error;
    }
}
/**
 * Read proper content from config file.
 * If the chartset of the config file is not utf-8, one error will be thrown.
 * @param {String} configPath
 * @return {String}
 */
function readConfigFileContent(configPath) {
    var rawBufContent = fs_1.default.readFileSync(configPath);
    if (!isUtf8(rawBufContent)) {
        throw new Error("The config file at \"".concat(configPath, "\" contains invalid charset, expect utf8"));
    }
    return stripBom(rawBufContent.toString("utf8"));
}
function isInTest() {
    return typeof global.it === "function";
}
function stripBom(string) {
    if (typeof string !== "string") {
        throw new TypeError("Expected a string, got ".concat(typeof string));
    }
    // Catches EFBBBF (UTF-8 BOM) because the buffer-to-string
    // conversion translates it to FEFF (UTF-16 BOM).
    if (string.charCodeAt(0) === 0xfeff) {
        return string.slice(1);
    }
    return string;
}
/**
 * Check if a Node.js Buffer or Uint8Array is UTF-8.
 */
function isUtf8(buf) {
    if (!buf) {
        return false;
    }
    var i = 0;
    var len = buf.length;
    while (i < len) {
        // UTF8-1 = %x00-7F
        if (buf[i] <= 0x7f) {
            i++;
            continue;
        }
        // UTF8-2 = %xC2-DF UTF8-tail
        if (buf[i] >= 0xc2 && buf[i] <= 0xdf) {
            // if(buf[i + 1] >= 0x80 && buf[i + 1] <= 0xBF) {
            if (buf[i + 1] >> 6 === 2) {
                i += 2;
                continue;
            }
            else {
                return false;
            }
        }
        // UTF8-3 = %xE0 %xA0-BF UTF8-tail
        // UTF8-3 = %xED %x80-9F UTF8-tail
        if (((buf[i] === 0xe0 && buf[i + 1] >= 0xa0 && buf[i + 1] <= 0xbf) ||
            (buf[i] === 0xed && buf[i + 1] >= 0x80 && buf[i + 1] <= 0x9f)) &&
            buf[i + 2] >> 6 === 2) {
            i += 3;
            continue;
        }
        // UTF8-3 = %xE1-EC 2( UTF8-tail )
        // UTF8-3 = %xEE-EF 2( UTF8-tail )
        if (((buf[i] >= 0xe1 && buf[i] <= 0xec) || (buf[i] >= 0xee && buf[i] <= 0xef)) &&
            buf[i + 1] >> 6 === 2 &&
            buf[i + 2] >> 6 === 2) {
            i += 3;
            continue;
        }
        // UTF8-4 = %xF0 %x90-BF 2( UTF8-tail )
        //          %xF1-F3 3( UTF8-tail )
        //          %xF4 %x80-8F 2( UTF8-tail )
        if (((buf[i] === 0xf0 && buf[i + 1] >= 0x90 && buf[i + 1] <= 0xbf) ||
            (buf[i] >= 0xf1 && buf[i] <= 0xf3 && buf[i + 1] >> 6 === 2) ||
            (buf[i] === 0xf4 && buf[i + 1] >= 0x80 && buf[i + 1] <= 0x8f)) &&
            buf[i + 2] >> 6 === 2 &&
            buf[i + 3] >> 6 === 2) {
            i += 4;
            continue;
        }
        return false;
    }
    return true;
}
//# sourceMappingURL=index.js.map