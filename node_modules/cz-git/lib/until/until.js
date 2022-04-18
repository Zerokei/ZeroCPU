"use strict";
/**
 * @description: provide until function
 * @author: @Zhengqbbb (zhengqbbb@gmail.com)
 * @license: MIT
 */
var __assign = (this && this.__assign) || function () {
    __assign = Object.assign || function(t) {
        for (var s, i = 1, n = arguments.length; i < n; i++) {
            s = arguments[i];
            for (var p in s) if (Object.prototype.hasOwnProperty.call(s, p))
                t[p] = s[p];
        }
        return t;
    };
    return __assign.apply(this, arguments);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.getValueByCallBack = exports.buildCommit = exports.handleStandardScopes = exports.handleCustomTemplate = exports.getMaxSubjectLength = exports.getProcessSubject = exports.log = void 0;
var wrap_1 = require("./wrap");
function log(type, msg) {
    var colorMapping = {
        info: "\u001B[32m",
        warm: "\u001B[33m",
        err: "\u001B[31m",
        reset: "\u001B[0m"
    };
    console.info("".concat(colorMapping[type], "[").concat(type, "]>>>: ").concat(msg).concat(colorMapping.reset));
}
exports.log = log;
var getProcessSubject = function (text) {
    var _a;
    return (_a = text.replace(/(^[\s]+|[\s\.]+$)/g, "")) !== null && _a !== void 0 ? _a : "";
};
exports.getProcessSubject = getProcessSubject;
var getEmojiStrLength = function (options, type) {
    var _a;
    var item = (_a = options.types) === null || _a === void 0 ? void 0 : _a.find(function (i) { return i.value === type; });
    // space
    return (item === null || item === void 0 ? void 0 : item.emoji) ? item.emoji.length + 1 : 0;
};
var countLength = function (target, typeLength, scope, emojiLength) {
    return target - typeLength - 2 - scope - emojiLength;
};
var getMaxSubjectLength = function (type, scope, options) {
    var optionMaxLength = Infinity;
    var typeLength = (type === null || type === void 0 ? void 0 : type.length) ? type.length : 0;
    var scopeLength = scope ? scope.length + 2 : 0;
    var emojiLength = options.useEmoji ? getEmojiStrLength(options, type) : 0;
    var maxHeaderLength = (options === null || options === void 0 ? void 0 : options.maxHeaderLength) ? options === null || options === void 0 ? void 0 : options.maxHeaderLength : Infinity;
    var maxSubjectLength = (options === null || options === void 0 ? void 0 : options.maxSubjectLength) ? options === null || options === void 0 ? void 0 : options.maxSubjectLength : Infinity;
    if ((options === null || options === void 0 ? void 0 : options.maxHeaderLength) === 0 || (options === null || options === void 0 ? void 0 : options.maxSubjectLength) === 0) {
        return 0;
    }
    else if (maxHeaderLength === Infinity) {
        return maxSubjectLength !== Infinity ? maxSubjectLength : Infinity;
    }
    else {
        optionMaxLength =
            countLength(maxHeaderLength, typeLength, scopeLength, emojiLength) < maxSubjectLength
                ? maxHeaderLength
                : maxSubjectLength;
    }
    return countLength(optionMaxLength, typeLength, scopeLength, emojiLength);
};
exports.getMaxSubjectLength = getMaxSubjectLength;
var filterCustomEmptyByOption = function (target, allowCustom, allowEmpty) {
    if (allowCustom === void 0) { allowCustom = true; }
    if (allowEmpty === void 0) { allowEmpty = true; }
    if (!Array.isArray(target) || target.length === 3 || target.length === 4) {
        return allowCustom ? target : target.filter(function (i) { return i.value !== "___CUSTOM___"; });
    }
    target = allowCustom ? target : target.filter(function (i) { return i.value !== "___CUSTOM___"; });
    return allowEmpty ? target : target.filter(function (i) { return i.value !== false; });
};
/**
 * @description: add separator custom empty
 */
var handleCustomTemplate = function (target, cz, align, emptyAlias, customAlias, allowCustom, allowEmpty) {
    if (align === void 0) { align = "top"; }
    if (emptyAlias === void 0) { emptyAlias = "empty"; }
    if (customAlias === void 0) { customAlias = "custom"; }
    if (allowCustom === void 0) { allowCustom = true; }
    if (allowEmpty === void 0) { allowEmpty = true; }
    var result = [
        { name: emptyAlias, value: false },
        { name: customAlias, value: "___CUSTOM___" },
        new cz.Separator()
    ];
    if (!Array.isArray(target)) {
        return result;
    }
    switch (align) {
        case "top":
            result = result.concat(target);
            break;
        case "bottom":
            result = target.concat(result.reverse());
            break;
        case "top-bottom":
            result = [{ name: emptyAlias, value: false }, new cz.Separator()]
                .concat(target)
                .concat([new cz.Separator(), { name: customAlias, value: "___CUSTOM___" }]);
            break;
        case "bottom-top":
            result = result = [{ name: customAlias, value: "___CUSTOM___" }, new cz.Separator()]
                .concat(target)
                .concat([new cz.Separator(), { name: emptyAlias, value: false }]);
            break;
        default:
            result = result.concat(target);
            break;
    }
    return filterCustomEmptyByOption(result, allowCustom, allowEmpty);
};
exports.handleCustomTemplate = handleCustomTemplate;
/**
 * @description: handle scope configuration option into standard options
 * @param {ScopesType}
 * @returns {Option[]}
 */
var handleStandardScopes = function (scopes) {
    return scopes.map(function (scope) {
        return typeof scope === "string"
            ? { name: scope, value: scope }
            : !scope.value
                ? __assign({ value: scope.name }, scope) : { value: scope.value, name: scope.name };
    });
};
exports.handleStandardScopes = handleStandardScopes;
var addType = function (type, colorize) {
    return colorize ? "\u001B[32m".concat(type, "\u001B[0m") : type;
};
var addScope = function (scope, colorize) {
    var separator = ":";
    if (!scope)
        return separator;
    scope = colorize ? "\u001B[33m".concat(scope, "\u001B[0m") : scope;
    return "(".concat(scope.trim(), ")").concat(separator);
};
var addEmoji = function (type, options) {
    var _a;
    if (options.useEmoji && type !== "") {
        var itemSource = ((_a = options.types) === null || _a === void 0 ? void 0 : _a.concat(options.typesAppend || [])) || [];
        var item = itemSource.find(function (i) { return i.value === type; });
        return (item === null || item === void 0 ? void 0 : item.emoji) ? " ".concat(item.emoji, " ") : " ";
    }
    else {
        return " ";
    }
};
var addSubject = function (subject, colorize) {
    if (!subject)
        return "";
    subject = colorize ? "\u001B[36m".concat(subject, "\u001B[0m") : subject;
    return subject.trim();
};
var addBreaklinesIfNeeded = function (value, breaklineChar) {
    if (breaklineChar === void 0) { breaklineChar = "|"; }
    return value.split(breaklineChar).join("\n").valueOf();
};
var addFooter = function (footer, footerPrefix, colorize) {
    if (footerPrefix === void 0) { footerPrefix = ""; }
    if (footerPrefix === "") {
        return colorize ? "\n\n\u001B[32m".concat(footer, "\u001B[0m") : "\n\n".concat(footer);
    }
    return colorize
        ? "\n\n\u001B[32m".concat(footerPrefix, " ").concat(footer, "\u001B[0m")
        : "\n\n".concat(footerPrefix, " ").concat(footer);
};
var buildCommit = function (answers, options, colorize) {
    var _a, _b, _c, _d, _e;
    if (colorize === void 0) { colorize = false; }
    var wrapOptions = {
        trim: true,
        newLine: "\n",
        indent: "",
        width: options.breaklineNumber
    };
    var head = addType((_a = answers.type) !== null && _a !== void 0 ? _a : "", colorize) +
        addScope(answers.scope, colorize) +
        addEmoji((_b = answers.type) !== null && _b !== void 0 ? _b : "", options) +
        addSubject(answers.subject, colorize);
    var body = (0, wrap_1.wrap)((_c = answers.body) !== null && _c !== void 0 ? _c : "", wrapOptions);
    var breaking = (0, wrap_1.wrap)((_d = answers.breaking) !== null && _d !== void 0 ? _d : "", wrapOptions);
    var footer = (0, wrap_1.wrap)((_e = answers.footer) !== null && _e !== void 0 ? _e : "", wrapOptions);
    var result = head;
    if (body) {
        result += "\n\n".concat(addBreaklinesIfNeeded(body, options.breaklineChar));
    }
    if (breaking) {
        result += "\n\nBREAKING CHANGE :\n".concat(addBreaklinesIfNeeded(breaking, options.breaklineChar));
    }
    if (footer) {
        result += addFooter(footer, answers.footerPrefix, colorize);
    }
    return result;
};
exports.buildCommit = buildCommit;
var getValueByCallBack = function (target, targetKey) {
    if (targetKey.length === 0)
        return target;
    targetKey.forEach(function (key) {
        var _a;
        if (!target[key])
            return;
        if (typeof target[key] === "function" && typeof target[key] !== "string") {
            return (target[key] = (_a = target === null || target === void 0 ? void 0 : target[key]) === null || _a === void 0 ? void 0 : _a.call(undefined));
        }
    });
    return target;
};
exports.getValueByCallBack = getValueByCallBack;
//# sourceMappingURL=until.js.map